package controller;

import details.DetailsEntry;
import main.Bot;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import product.Laptop;
import product.Phone;
import product.Product;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class ControlMainScreen implements Initializable {
    @FXML private Button Compare;
    @FXML private Button showAll;
    @FXML private Label addLabel;
    @FXML private AnchorPane addPane1;
    @FXML private AnchorPane OptionPane1;
    @FXML private AnchorPane OptionPane2;
    @FXML private HBox myBrandHBox;
    @FXML private HBox myProductHBox;
    @FXML private AnchorPane myPane1;
    @FXML private AnchorPane myPane2;
    @FXML private AnchorPane myMainPane;
    @FXML private TextField searchTextField;
    @FXML private AnchorPane searchPane;
    @FXML private GridPane gridProductPane;
    @FXML private ScrollPane scrollProductPane;
    @FXML private StackPane addStackPane;
    @FXML private ImageView addImage1;
    @FXML private TableView<List<String>> compareTable;
    @FXML private TableColumn<List<String>, String> detailsColumn;
    @FXML private TableColumn<List<String>, String> firstProductColumn;
    @FXML private TableColumn<List<String>, String> secondProductColumn;
    @FXML private AnchorPane sortPane;
    @FXML private Button blinkButton;

    // Class variables
    private static Product<?> productAddedFirst;
    private static ArrayList<Phone> phoneList;
    private static ArrayList<Laptop> laptopList;
    private static ArrayList<Product<?>> productList = new ArrayList<>();
    private static TreeItem<String> productTree;
    private static TreeSet<String> productBrand = new TreeSet<>();
    private static TreeSet<String> productType = new TreeSet<>();
    private static Stage stage;
    private final int maxPerRow = 5;
    private int stt = 0;
    private static int numberProductAdded = 1;

    // Lists to manage dynamic buttons
    private List<Button> dynamicTypeButtons = new ArrayList<>();
    private List<Button> dynamicBrandButtons = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        crawl();
        createTree();
        initialize_button();
        createSearchButton();

        myProductHBox.setSpacing(20);
        myProductHBox.setAlignment(Pos.CENTER);
        myBrandHBox.setSpacing(20);
        myBrandHBox.setAlignment(Pos.CENTER);

        scrollProductPane.setFitToWidth(true);

        showAllProduct(productList);
        searchBySearchBox();

        showOptionPane1();
        addStackPane.setVisible(false);
        myPane2.setVisible(false);
    }

    @FXML
    public void openChatbot() {
        Bot chatbot = new Bot();
        Stage chatStage = new Stage();
        chatbot.createChatUI(chatStage);
        chatStage.show();
    }

    @FXML
    private void showOptionPane1(){
        OptionPane1.setVisible(true);
        OptionPane2.setVisible(false);
        myBrandHBox.getChildren().removeAll(dynamicBrandButtons);
        dynamicBrandButtons.clear();
    }

    @FXML
    private void showOptionPane2(){
        OptionPane1.setVisible(false);
        OptionPane2.setVisible(true);
    }

    @FXML
    private void switchPane1(ActionEvent e){
        addStackPane.setVisible(true);
        addPane1.setVisible(true);
        myPane2.setVisible(false);
        myPane1.setVisible(false);
        sortPane.setVisible(false);
        // Hide main elements
        searchPane.setVisible(false);
        scrollProductPane.setVisible(false);
        myProductHBox.getParent().getParent().setVisible(false);

    }
    @FXML
    private void switchPane2(ActionEvent e){
        myPane1.setVisible(true);
        sortPane.setVisible(true);
        myPane2.setVisible(false);
        addPane1.setVisible(false);
        addStackPane.setVisible(false);
        searchPane.setVisible(true);
        scrollProductPane.setVisible(true);
        myProductHBox.getParent().getParent().setVisible(true);
        showOptionPane1();
    }

    @FXML
    private void switchSearchInCompare(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/screen/ShowSearchInCompare.fxml"));
            Parent root = loader.load();

            SearchCompareController searchController = loader.getController();
            searchController.initData(this, productList, productBrand, productTree);

            Stage searchStage = new Stage();
            searchStage.setTitle("Chọn sản phẩm để so sánh");
            searchStage.setScene(new Scene(root));
            searchStage.initModality(Modality.APPLICATION_MODAL);
            searchStage.showAndWait();

        } catch (IOException ex) {
            System.err.println("Không thể mở cửa sổ tìm kiếm!");
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể mở cửa sổ tìm kiếm.");
            alert.setContentText("Đã xảy ra lỗi khi tải giao diện.");
            alert.showAndWait();
        }
    }

    @FXML
    public void gainProductFromAdd(int order){
        if (order < 0 || order >= productList.size()) return;

        Product<?> selectedProduct = productList.get(order);

        if (numberProductAdded % 2 == 1) {
            addLabel.setText("Đã có 1 sản phẩm được thêm: " + selectedProduct.getName());
            addLabel.setVisible(true);
            productAddedFirst = selectedProduct;
            addImage1.setImage(new Image(selectedProduct.getPicture_url(), true));
            numberProductAdded++;
        } else {
            if (productAddedFirst != null && productAddedFirst.getType().equals(selectedProduct.getType())) {
                addPane1.setVisible(false);
                myPane2.setVisible(true);
                addStackPane.setVisible(true);
                myPane1.setVisible(false);
                customCompareTable(productAddedFirst, selectedProduct);
            } else {
                productAddedFirst = selectedProduct;
                numberProductAdded = 2;
                addLabel.setText("Đã có 1 sản phẩm được thêm: " + selectedProduct.getName());
                addImage1.setImage(new Image(selectedProduct.getPicture_url(), true));
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Cảnh báo");
                alert.setHeaderText("Loại sản phẩm không khớp.");
                alert.setContentText("Đã chọn sản phẩm mới. Vui lòng chọn sản phẩm thứ hai cùng loại.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void clearAddPane(){
        compareTable.getColumns().clear();
        compareTable.getItems().clear();
        productAddedFirst = null;
        numberProductAdded = 1;
        addLabel.setVisible(false);
        addImage1.setImage(null);
        addPane1.setVisible(true);
        myPane2.setVisible(false);
    }

    private void customCompareTable(Product<?> product1, Product<?> product2) {
        compareTable.getColumns().clear();
        compareTable.getItems().clear();
        compareTable.setFixedCellSize(Region.USE_COMPUTED_SIZE);

        List<DetailsEntry> d1 = product1.allDetails();
        List<DetailsEntry> d2 = product2.allDetails();

        ArrayList<List<String>> arr = new ArrayList<>();
        arr.add(Arrays.asList("", product1.getPicture_url(), product2.getPicture_url()));

        int maxSize = Math.max(d1.size(), d2.size());
        for (int i = 0; i < maxSize; i++) {
            String label = (i < d1.size()) ? d1.get(i).getLabel() : (i < d2.size() ? d2.get(i).getLabel() : "N/A");
            String val1 = (i < d1.size()) ? d1.get(i).getValue() : "N/A";
            String val2 = (i < d2.size()) ? d2.get(i).getValue() : "N/A";
            arr.add(Arrays.asList(label, val1, val2));
        }

        detailsColumn.setText("Thông số");
        firstProductColumn.setText(product1.getName());
        secondProductColumn.setText(product2.getName());

        setColumn(detailsColumn, arr, 0);
        setColumn(firstProductColumn, arr, 1);
        setColumn(secondProductColumn, arr, 2);

        compareTable.getColumns().addAll(detailsColumn, firstProductColumn, secondProductColumn);

        ObservableList<List<String>> data = FXCollections.observableArrayList(arr);
        compareTable.setItems(data);
    }

    @FXML
    public void searchByButton(ActionEvent e){
        showAllProduct(productList);
        showOptionPane1();
    }

    @FXML
    public void searchBySearchBox(){
        searchTextField.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.ENTER)){
                String searchBox = searchTextField.getText().toLowerCase().trim();
                if (searchBox.isEmpty()) {
                    showAllProduct(productList);
                    return;
                }
                ArrayList<Product<?>> searchResult = new ArrayList<>();
                for(Product<?> product : productList){
                    if(product.getName().toLowerCase().contains(searchBox)){
                        searchResult.add(product);
                    }
                }
                if(!searchResult.isEmpty()){ showSearchResult(searchResult); } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thông báo");
                    alert.setHeaderText("Không tìm thấy sản phẩm.");
                    alert.setContentText("Không có sản phẩm nào khớp với: '" + searchTextField.getText() + "'");
                    alert.showAndWait();
                }
            }
        });
    }

    private void showSearchResult(ArrayList<Product<?>> productsToShow) {
        gridProductPane.getChildren().clear();
        int numberOfProduct = productsToShow.size();
        int col = 0;
        int row = 0;
        for (int i = 0; i < numberOfProduct; i++) {
            int originalIndex = productList.indexOf(productsToShow.get(i));
            if (originalIndex != -1) {
                createProductButton(numberOfProduct, originalIndex, col, row);
                col++;
                if (col >= maxPerRow) { col = 0; row++; }
            }
        }
    }

    @FXML
    public void sortProductPrice(Event e){
        MenuItem btn = (MenuItem) e.getSource();
        String sortType = btn.getText();

        productList.sort((o1, o2) -> {
            try {
                long price1 = Long.parseLong(o1.getPrice().replaceAll("[^\\d]", ""));
                long price2 = Long.parseLong(o2.getPrice().replaceAll("[^\\d]", ""));
                if(sortType.charAt(0) == 'C')
                    return Long.compare(price2, price1);
                else
                    return Long.compare(price1, price2);
            } catch (Exception ex) {
                System.err.println("Error parsing price: " + o1.getPrice() + " or " + o2.getPrice());
                return 0;
            }
        });

        createTree();
        createSearchButton();
        showAllProduct(productList);
    }

    private void crawl(){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            InputStream inputStreamPhones = getClass().getResourceAsStream("/data/phones_filtered.json");
            if (inputStreamPhones != null) {
                phoneList = objectMapper.readValue(inputStreamPhones, new TypeReference<>() {});
                productList.addAll(phoneList);
            } else { System.err.println("Cannot load phones_filtered.json"); }

            InputStream inputStreamLaptops = getClass().getResourceAsStream("/data/laptop_filtered.json");
            if (inputStreamLaptops != null) {
                laptopList = objectMapper.readValue(inputStreamLaptops, new TypeReference<>() {});
                productList.addAll(laptopList);
            } else { System.err.println("Cannot load laptop_filtered.json"); }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể tải dữ liệu sản phẩm.");
            alert.setContentText("Ứng dụng có thể không hoạt động đúng.");
            alert.showAndWait();
        }
    }

    private void setColumn(TableColumn<List<String>, String> col, ArrayList<List<String>> arr, int index) {
        col.setCellValueFactory(cellData -> {
            List<String> rowValue = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    (rowValue != null && index < rowValue.size()) ? rowValue.get(index) : null
            );
        });

        col.setCellFactory(column -> new TableCell<List<String>, String>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                int currentIndex = getIndex();
                if (empty || currentIndex < 0 || currentIndex >= arr.size()) {
                    setText(null); setGraphic(null); setStyle("");
                } else {
                    String currentItem = arr.get(currentIndex).get(index);
                    if (currentItem == null) currentItem = "";

                    if (currentIndex == 0) {
                        if (index > 0 && !currentItem.isEmpty()) {
                            try {
                                imageView.setImage(new Image(currentItem, true));
                                imageView.setFitWidth(150); imageView.setFitHeight(150);
                                imageView.setPreserveRatio(true);
                                setGraphic(imageView); setText(null); setAlignment(Pos.CENTER);
                            } catch (Exception e) {
                                setText("No Image"); setGraphic(null); setAlignment(Pos.CENTER);
                            }
                        } else { setText(""); setGraphic(null); }
                        setPrefHeight(160); setStyle("-fx-border-color: lightgrey; -fx-border-width: 0 0 1 0;");
                    } else {
                        setText(currentItem); setGraphic(null); setAlignment(Pos.CENTER_LEFT);
                        setPrefHeight(Control.USE_COMPUTED_SIZE);
                        setStyle(currentIndex % 2 == 0 ? "-fx-background-color: #f9f9f9;" : "-fx-background-color: white;");
                        if (index == 0) { setStyle(getStyle() + "-fx-font-weight: bold;"); }
                    }
                }
            }
        });
    }

    protected ObservableList<TreeItem<String>> bfs(TreeItem<String> startNode, String check) {
        Queue<TreeItem<String>> queue = new ArrayDeque<>();
        if (startNode != null) queue.add(startNode);
        while (!queue.isEmpty()) {
            TreeItem<String> current = queue.poll();
            if (current.getValue().equalsIgnoreCase(check)) {
                ArrayList<TreeItem<String>> result = new ArrayList<>();
                for (TreeItem<String> child : current.getChildren()) {
                    if (child.getChildren().isEmpty()) return current.getChildren();
                    else result.addAll(child.getChildren());
                }
                return FXCollections.observableArrayList(result);
            } else {
                queue.addAll(current.getChildren());
            }
        }
        return null;
    }

    private void addProductToTree(Product<?> product, TreeItem<String> itemTree){
        String brand = product.getBrand().toLowerCase();
        TreeItem<String> brandNode = null;
        for(TreeItem<String> itemBrand : itemTree.getChildren()){
            if(itemBrand.getValue().equals(brand)){ brandNode = itemBrand; break; }
        }
        if(brandNode == null){
            brandNode = new TreeItem<>(brand);
            itemTree.getChildren().add(brandNode);
            productBrand.add(brand);
        }
        TreeItem<String> order = new TreeItem<>(""+ stt);
        brandNode.getChildren().add(order);
        stt++;
    }

    public void initialize_button() {
        Timeline blink = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> blinkButton.setStyle(
                        "-fx-background-color: #64b5f6;" +
                                "-fx-text-fill: white;" +
                                "-fx-background-radius: 20;"
                )),
                new KeyFrame(Duration.seconds(1.0), e -> blinkButton.setStyle(
                        "-fx-background-color: #C0C0C0;" +
                                "-fx-text-fill: black;" +
                                "-fx-background-radius: 20;" +
                                "-fx-border-radius: 20;"
                ))
        );
        blinkButton.setOnMouseEntered(e -> { blinkButton.setScaleX(1.2); blinkButton.setScaleY(1.2); });
        blinkButton.setOnMouseExited(e -> { blinkButton.setScaleX(1); blinkButton.setScaleY(1); });
        blink.setCycleCount(Timeline.INDEFINITE);
        blink.play();
    }

    private void createTree(){
        productBrand.clear();
        productType.clear();
        productTree = new TreeItem<>("product");
        stt = 0;

        for(Product<?> product : productList){
            String type = product.getType();
            if (type == null) continue;

            TreeItem<String> typeNode = null;
            for(TreeItem<String> itemType : productTree.getChildren()){
                if(itemType.getValue().equals(type)){ typeNode = itemType; break; }
            }
            if(typeNode == null){
                typeNode = new TreeItem<>(type);
                productTree.getChildren().add(typeNode);
                productType.add(type);
            }
            addProductToTree(product, typeNode);
        }
        Comparator<TreeItem<String>> sorter = Comparator.comparing(TreeItem::getValue);
        productTree.getChildren().sort(sorter);
        for(TreeItem<String> type : productTree.getChildren()){ type.getChildren().sort(sorter); }
    }

    private void createSearchButton() {
        // Remove only old dynamic buttons
        myProductHBox.getChildren().removeAll(dynamicTypeButtons);
        dynamicTypeButtons.clear();
        myBrandHBox.getChildren().removeAll(dynamicBrandButtons);
        dynamicBrandButtons.clear();

        ObservableList<TreeItem<String>> types = productTree.getChildren();

        for(TreeItem<String> type : types){
            Button btn = new Button(type.getValue());
            btn.setOnAction(e -> {
                showOptionPane2();
                myBrandHBox.getChildren().removeAll(dynamicBrandButtons);
                dynamicBrandButtons.clear();

                ObservableList<TreeItem<String>> items = bfs(productTree, type.getValue());
                if (items != null) { showProductsFromTreeItems(items); }

                Button returnButton = new Button("Return");
                returnButton.setOnAction(actionEvent -> {
                    showAllProduct(productList);
                    showOptionPane1();
                });
                returnButton.setPrefHeight(27);
                myBrandHBox.getChildren().add(returnButton);
                dynamicBrandButtons.add(returnButton);

                for(TreeItem<String> brand : type.getChildren()){
                    Button btn1 = new Button(brand.getValue());
                    btn1.setOnAction(e1 -> {
                        Button press = (Button) e1.getSource();
                        ObservableList<TreeItem<String>> brandItems = bfs(productTree, press.getText());
                        if (brandItems != null) { showProductsFromTreeItems(brandItems); }
                    });
                    btn1.setPrefHeight(27);
                    myBrandHBox.getChildren().add(btn1);
                    dynamicBrandButtons.add(btn1);
                }
            });
            btn.setPrefHeight(27);
            myProductHBox.getChildren().add(btn);
            dynamicTypeButtons.add(btn);
        }
    }

    private void showProductsFromTreeItems(ObservableList<TreeItem<String>> items) {
        gridProductPane.getChildren().clear();
        int numberOfProduct = items.size();
        int col = 0; int row = 0;
        try {
            for (TreeItem<String> item : items) {
                int index = Integer.parseInt(item.getValue());
                if (index >= 0 && index < productList.size()) {
                    createProductButton(numberOfProduct, index, col, row);
                    col++;
                    if (col >= maxPerRow) { col = 0; row++; }
                }
            }
        } catch (Exception ex) {
            System.err.println("Error showing products from tree: " + ex.getMessage());
        }
    }

    protected VBox createProductItem(Product<?> product) {
        VBox vBox = new VBox(10);
        vBox.setPrefWidth(180); vBox.setMinHeight(360); vBox.setMaxHeight(360);
        vBox.setAlignment(Pos.TOP_CENTER); vBox.setPadding(new Insets(10));
        vBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-background-color: white;");

        ImageView imageView = new ImageView();
        try {
            imageView.setImage(new Image(product.getPicture_url(), true));
            imageView.setFitWidth(160); imageView.setFitHeight(160);
            imageView.setPreserveRatio(true);
        } catch (Exception e) { System.err.println("Lỗi tải ảnh: " + product.getPicture_url()); }
        VBox.setVgrow(imageView, Priority.NEVER);

        Label nameLabel = new Label(product.getName());
        nameLabel.setWrapText(true); nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        nameLabel.setAlignment(Pos.CENTER); nameLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        nameLabel.setMinHeight(60); nameLabel.setMaxWidth(160);
        VBox.setVgrow(nameLabel, Priority.ALWAYS);

        Label priceLabel = new Label(standardization(product.getPrice()));
        priceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #e53935;");
        priceLabel.setAlignment(Pos.CENTER); VBox.setVgrow(priceLabel, Priority.NEVER);

        HBox ratingBox = new HBox(5);
        ratingBox.setAlignment(Pos.CENTER);
        Label ratingLabel = new Label(String.format("%.1f", product.getAverage_rating()));
        ratingLabel.setStyle("-fx-font-size: 12px;");
        Label starLabel = new Label("⭐");
        starLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #fdd835;");
        Label countLabel = new Label("(" + product.getTotal_count() + ")");
        countLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: grey;");
        ratingBox.getChildren().addAll(ratingLabel, starLabel, countLabel);
        VBox.setVgrow(ratingBox, Priority.NEVER);

        vBox.getChildren().addAll(imageView, nameLabel, priceLabel, ratingBox);
        return vBox;
    }

    private void createProductButton(int totalOrder, int cnt, int j, int i){
        int totalRows = (totalOrder + maxPerRow - 1) / maxPerRow;
        Button button = new Button("" + cnt);
        button.setOnAction(event -> {
            Thread thread = new Thread(() -> {
                try { Thread.sleep(100);
                    Platform.runLater(() -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/screen/ShowProduct.fxml")); // Use ShowProduct.fxml
                            Parent root = loader.load();
                            ProductController control = loader.getController();
                            control.setProduct(productList.get(cnt)); // Use setProduct
                            Scene scene = new Scene(root); Stage stage = new Stage();
                            stage.setScene(scene); stage.setTitle(productList.get(cnt).getName());
                            stage.show();
                        } catch (Exception e) { e.printStackTrace(); }
                    });
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); e.printStackTrace(); }
            });
            thread.setDaemon(true); thread.start();
        });

        VBox productBox = createProductItem(productList.get(cnt)); // Renamed
        AnchorPane pane2 = new AnchorPane(button);
        AnchorPane.setTopAnchor(button, 0.0); AnchorPane.setBottomAnchor(button, 0.0);
        AnchorPane.setLeftAnchor(button, 0.0); AnchorPane.setRightAnchor(button, 0.0);
        button.setOpacity(0);
        gridProductPane.setPrefHeight(380 * totalRows);
        gridProductPane.setGridLinesVisible(false);

        StackPane stack = new StackPane();
        stack.setMinSize(180, 360); stack.getChildren().addAll(productBox,pane2);
        GridPane.setHgrow(stack, Priority.ALWAYS); GridPane.setVgrow(stack, Priority.ALWAYS);
        gridProductPane.add(stack, j, i);
    }

    private void showAllProduct(ArrayList<Product<?>> products){
        gridProductPane.getChildren().clear();
        int totalProducts = products.size(); int row = 0; int col = 0;
        for (int cnt = 0; cnt < totalProducts; cnt++) {
            createProductButton(totalProducts, cnt, col, row);
            col++;
            if (col >= maxPerRow) { col = 0; row++; }
        }
    }

    public String standardization(String price){
        if (price == null || price.isEmpty()) return "N/A";
        try {
            String currency = ""; String numberPart = price;
            if (!Character.isDigit(price.charAt(price.length() - 1))) {
                currency = " " + price.charAt(price.length() - 1);
                numberPart = price.substring(0, price.length() - 1);
            }
            long number = Long.parseLong(numberPart.replaceAll("[^\\d]", ""));
            return String.format("%,d", number) + currency;
        } catch (Exception e) { return price; }
    }

    public TreeSet<String> getProductBrand() { return productBrand; }
    public TreeItem<String> getProductTree() { return productTree; }
    public ArrayList<Product<?>> getProductList() { return productList; }
    public StackPane getAddStackPane() { return addStackPane; }
    public AnchorPane getAddPane1() { return addPane1; }
    public AnchorPane getMyPane1() { return myPane1; }
    public AnchorPane getSortPane() { return sortPane; }

    public static void setStage(Stage stage) {
        ControlMainScreen.stage = stage;
    }

}