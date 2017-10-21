package productcatalog;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class ProductCatalog extends javax.swing.JFrame implements CatalogContract.View {

    private final CatalogPresenter catalogPresenter;
    private DefaultTableModel defaultTableModel;
    private final String[] colors = {"-ANY-", "BLACK", "BLUE", "BROWN", "GRAY", "GREEN", "MAGENTA", "ORANGE", "RED", "WHITE", "YELLOW"};
    private TableRowSorter<TableModel> rowSorter;

    /**
     * Creates new form ProductsCatalog
     */
    private ProductCatalog() {
        catalogPresenter = new CatalogPresenter(new CatalogRepositoryDb(), this);
        initComponents();
        setupTable();
        fillComboBoxes();
        disableButons();
        catalogPresenter.getCategoriesFromDb();
        addRowSelectionListener();
        setupSearchTextFields();
        setupNavigationButtons();
        lockLowerDate();
    }

    private void setupNavigationButtons() {
        previousPageButton.add(new BasicArrowButton(BasicArrowButton.WEST));
        nextPageButton.add(new BasicArrowButton(BasicArrowButton.EAST));
    }

    private void setupTable() {
        createTableModel();
        populateProductsJTable();
    }

    private void fillComboBoxes() {
        fillCategoryComboBoxes();
        fillColorSetterComboBox();
    }

    private void disableButons() {
        modifyProductButton.setEnabled(false);
        deleteProductButton.setEnabled(false);
    }

    private void setupSearchTextFields() {
        searchPriceToTextField.setFocusLostBehavior(JFormattedTextField.PERSIST);
        searchPriceFromTextField.setFocusLostBehavior(JFormattedTextField.PERSIST);
    }

    private void lockLowerDate() {
        fromDatePicker.addPropertyChangeListener("date", (PropertyChangeEvent evt) -> {
            Date selectedDate = fromDatePicker.getDate();
            toDatePicker.getMonthView().setLowerBound(selectedDate);
        });
    }

    private void populateProductsJTable() {
        try {
            catalogPresenter.getProductsFromDb();
            setFoundProductsNumber(productsCatalogTable.getRowCount());
        } catch (Exception e) {
            System.out.println("populateProductsJTable " + e);
        }
    }

    private void createTableModel() {
        Object[] columns = new Object[]{"ID", "NAME", "PRICE", "COLOR", "IN STOCK", "EXPIRING DATE", "CATEGORY", "CATEGORY ID"};
        defaultTableModel = new DefaultTableModel(columns, 0);
        productsCatalogTable.setModel(defaultTableModel);
        productsCatalogTable.setAutoCreateRowSorter(true);
        productsCatalogTable.setDefaultEditor(Object.class, null);
        rowSorter = new TableRowSorter<>(productsCatalogTable.getModel());
        hideIdColumns();
    }

    public void hideIdColumns() {
        productsCatalogTable.getColumnModel().getColumn(0).setMinWidth(0);
        productsCatalogTable.getColumnModel().getColumn(0).setMaxWidth(0);
        productsCatalogTable.getColumnModel().getColumn(0).setWidth(0);
        productsCatalogTable.getColumnModel().getColumn(7).setMinWidth(0);
        productsCatalogTable.getColumnModel().getColumn(7).setMaxWidth(0);
        productsCatalogTable.getColumnModel().getColumn(7).setWidth(0);
    }

    private void refreshJTable() {
        try {
            defaultTableModel.setRowCount(0);
            catalogPresenter.getProductsFromDb();
        } catch (Exception e) {
            System.out.println("refreshJTable " + e.getMessage());
        }
    }

    private void searchProduct() {
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        String name = TextUtils.isEmpty(searchNameTextField.getText()) ? null : searchNameTextField.getText();
        double lowerPrice = -1;
        double higherPrice = -1;
        if (!"".equals(searchPriceFromTextField.getText())) {
            lowerPrice = Double.parseDouble(searchPriceFromTextField.getText());
        }
        if (!"".equals(searchPriceToTextField.getText())) {
            higherPrice = Double.parseDouble(searchPriceToTextField.getText());
        }
        String color = colorFilterComboBox.getSelectedItem().toString();
        String category = categoryFilterComboBox.getSelectedItem().toString();
        boolean inStock = inStockCheckBox.isSelected();
        Date lowerExpiringDate = fromDatePicker.getDate();
        Date higherExpiringDate = toDatePicker.getDate();
        if (name != null) {
            System.out.println("name " + name);
            filters.add(RowFilter.regexFilter("(?i)" + name, 1));
        }
        if (lowerPrice >= 0) {
            System.out.println("lowerPrice " + lowerPrice);
            filters.add(RowFilter.numberFilter(RowFilter.ComparisonType.AFTER, lowerPrice - 1, 2));
        }
        if (higherPrice >= 0) {
            System.out.println("higherPrice " + higherPrice);
            filters.add(RowFilter.numberFilter(RowFilter.ComparisonType.BEFORE, higherPrice + 1, 2));
        }
        if (!"-ANY-".equals(color)) {
            System.out.println("color " + color);
            filters.add(RowFilter.regexFilter(color, 3));
        } else if ("-ANY-".equals(color)) {
            filters.remove(RowFilter.regexFilter(color, 3));
        }
        if (inStock) {
            filters.add(RowFilter.regexFilter(String.valueOf(inStock), 4));
        }
        if (lowerExpiringDate != null) {
            System.out.println("lowerExpiringDate " + lowerExpiringDate);
            filters.add(RowFilter.dateFilter(RowFilter.ComparisonType.AFTER, lowerExpiringDate, 5));
        }
        if (higherExpiringDate != null) {
            System.out.println("higherExpiringDate " + higherExpiringDate);
            filters.add(RowFilter.dateFilter(RowFilter.ComparisonType.BEFORE, higherExpiringDate, 5));
        }
        if (!"-ANY-".equals(category)) {
            System.out.println("category " + category);
            filters.add(RowFilter.regexFilter(category, 6));
        } else if ("-ANY-".equals(category)) {
            filters.remove(RowFilter.regexFilter(category, 6));
        }
        RowFilter<Object, Object> productFilter = RowFilter.andFilter(filters);
        rowSorter.setRowFilter(productFilter);
        productsCatalogTable.setRowSorter(rowSorter);
        setFoundProductsNumber(productsCatalogTable.getRowCount());
    }

    private void setFoundProductsNumber(int foundProductsNumber) {
        switch (foundProductsNumber) {
            case 1:
                totalRecordsLabel.setText("Total " + foundProductsNumber + " product");
                break;
            default:
                totalRecordsLabel.setText("Total " + foundProductsNumber + " products");
        }
    }

    private void deleteProducts() {
        int[] selectedRows = productsCatalogTable.getSelectedRows();
        String selectedProductsId = "";

        for (int i = 0; i < selectedRows.length; i++) {
            selectedProductsId += productsCatalogTable.getValueAt(selectedRows[i], 0).toString();
            if (i < selectedRows.length - 1) {
                selectedProductsId += ",";
            }
        }
        catalogPresenter.deleteProduct(selectedProductsId);
    }

    private void addRowSelectionListener() {
        productsCatalogTable.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
            int[] selection = productsCatalogTable.getSelectedRows();
            if (selection.length == 1) {
                modifyProductButton.setEnabled(true);
                deleteProductButton.setEnabled(true);
            }
            if (selection.length > 1) {
                deleteProductButton.setEnabled(true);
                modifyProductButton.setEnabled(false);
            }
            if (selection.length < 1) {
                deleteProductButton.setEnabled(false);
                modifyProductButton.setEnabled(false);
            }
        });
    }

    @Override
    public void displayAddProductForm() {
        showAddProductDialog();
        setExpiringDatePicker.getMonthView().setLowerBound(new Date());
    }

    private void showAddProductDialog() {
        setColorComboBox.setSelectedIndex(-1);
        setCategoryComboBox.setSelectedIndex(-1);
        addProductDialog.setTitle("Add Product");
        addProductDialog.setLocationRelativeTo(this);
        addProductDialog.setVisible(true);
    }

    private Product getProductInfo() {
        int selectedRow = productsCatalogTable.getSelectedRow();
        int productId = (int) productsCatalogTable.getValueAt(selectedRow, 0);
        String name = productsCatalogTable.getValueAt(selectedRow, 1).toString();
        double price = (double) productsCatalogTable.getValueAt(selectedRow, 2);
        String color = (String) productsCatalogTable.getValueAt(selectedRow, 3);
        boolean inStock = (boolean) productsCatalogTable.getValueAt(selectedRow, 4);
        Date expiringDate = (Date) productsCatalogTable.getValueAt(selectedRow, 5);
        String categoryName = (String) productsCatalogTable.getValueAt(selectedRow, 6);
        int categoryId = (int) productsCatalogTable.getValueAt(selectedRow, 7);
        Product product = new Product.ProductBuilder()
                .id(productId)
                .name(name)
                .price(price)
                .color(color)
                .inStock(inStock)
                .expiringDate(expiringDate)
                .categoryName(categoryName)
                .categoryId(categoryId)
                .build();
        return product;
    }

    public void fillModifyProductFields(Product product) {
        if (productsCatalogTable.getSelectedRow() != -1) {
            showModifyProductDialog();
            nameTextField.setText(product.getName());
            priceTextField.setText(String.valueOf(product.getPrice()));
            setColorComboBox.setSelectedItem(product.getColor());
            setInStockCheckBox.setSelected(product.getInStock());
            setExpiringDatePicker.setDate(product.getExpiringDate());
            setCategoryComboBox.setSelectedItem(product.getCategoryName());
        }
    }

    private Product createProductFromFieldsEntry() {
        String categoryName = null, color = null;
        int categoryId = -1;
        String name = TextUtils.isEmpty(nameTextField.getText()) ? null : nameTextField.getText();
        double price = TextUtils.isEmpty(priceTextField.getText()) ? -1 : Double.parseDouble(priceTextField.getText());
        boolean inStock = setInStockCheckBox.isSelected();
        Date expiringDate = setExpiringDatePicker.getDate();
        if (setColorComboBox.getSelectedIndex() >= 0) {
            color = setColorComboBox.getSelectedItem().toString();
        }
        if (setCategoryComboBox.getSelectedIndex() >= 0) {
            categoryName = setCategoryComboBox.getSelectedItem().toString();
            categoryId = (catalogPresenter.getCategoriesFromDb().indexOf(categoryName)) + 1;
        }
        Product product = new Product.ProductBuilder()
                .name(name)
                .price(price)
                .color(color)
                .inStock(inStock)
                .expiringDate(expiringDate)
                .categoryName(categoryName)
                .categoryId(categoryId)
                .build();
        return product;
    }

    private void showModifyProductDialog() {
        addProductDialog.setTitle("Modify Product");
        addProductDialog.setLocationRelativeTo(this);
        addProductDialog.setVisible(true);
    }

    private void emptyFields() {
        nameTextField.setText("");
        priceTextField.setText("");
        setExpiringDatePicker.setDate(null);
    }


//    private void enableMainWindow() {
//        this.setEnabled(true);
//    }

    public void confirmAddProduct() {
        catalogPresenter.addProduct(createProductFromFieldsEntry());
        refreshJTable();
        emptyFields();
    }

    private void confirmModifyProduct(Product product, int productId) {
        catalogPresenter.modifyProduct(product, productId);
        refreshJTable();
        emptyFields();
        addProductDialog.dispose();
    }

    @Override
    public void displayProductTable(ArrayList<Product> products) {
        products.forEach((product) -> {
            defaultTableModel.insertRow(productsCatalogTable.getRowCount(),
                    new Object[]{product.getId(), product.getName(), product.getPrice(), product.getColor(), product.getInStock(),
                        product.getExpiringDate(), product.getCategoryName(), product.getCategoryId()});
        });
    }

    private void fillCategoryComboBoxes() {
        ArrayList<String> categoriesList = catalogPresenter.getCategoriesFromDb();
        categoryFilterComboBox.setModel(new DefaultComboBoxModel(categoriesList.toArray()));
        categoriesList.remove(0);
        setCategoryComboBox.setModel(new DefaultComboBoxModel(categoriesList.toArray()));
    }

    private void fillColorSetterComboBox() {
        colorFilterComboBox.setModel(new DefaultComboBoxModel(colors));
        ArrayList<String> colorsList = new ArrayList<>(Arrays.asList(colors));
        colorsList.remove(0);
        setColorComboBox.setModel(new DefaultComboBoxModel(colorsList.toArray()));
    }

    @Override
    public void displayErrorMessage(String errorMessage) {
        StringBuilder error = new StringBuilder();
        error.append("Please enter product ");
        error.append(errorMessage);
        error.append("!");
        JOptionPane.showMessageDialog(null, error, "Add product error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void closeAddProductDialog() {
        addProductDialog.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        addProductDialog = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        priceLabel = new javax.swing.JLabel();
        colorLabel = new javax.swing.JLabel();
        inStockLabel = new javax.swing.JLabel();
        expirationDateLabel = new javax.swing.JLabel();
        categoryLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        priceTextField = new javax.swing.JFormattedTextField();
        setInStockCheckBox = new javax.swing.JCheckBox();
        buttonsPanel = new javax.swing.JPanel();
        okAddProductButton = new javax.swing.JButton();
        cancelAddProductButton = new javax.swing.JButton();
        setColorComboBox = new javax.swing.JComboBox<>();
        setCategoryComboBox = new javax.swing.JComboBox<>();
        setExpiringDatePicker = new org.jdesktop.swingx.JXDatePicker();
        tableTitleLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        productsCatalogTable = new javax.swing.JTable();
        mainWindowButtonsPanel = new javax.swing.JPanel();
        addProductButton = new javax.swing.JButton();
        modifyProductButton = new javax.swing.JButton();
        deleteProductButton = new javax.swing.JButton();
        exitApplicationButton = new javax.swing.JButton();
        unsortTableButton = new javax.swing.JButton();
        searchPanel = new javax.swing.JPanel();
        searchNameLabel = new javax.swing.JLabel();
        searchNameTextField = new javax.swing.JTextField();
        searchPriceLabel = new javax.swing.JLabel();
        searchPriceFromTextField = new javax.swing.JFormattedTextField();
        searchPriceToLabel = new javax.swing.JLabel();
        searchPriceToTextField = new javax.swing.JFormattedTextField();
        searchColorLabel = new javax.swing.JLabel();
        searchDateLabel = new javax.swing.JLabel();
        fromDatePicker = new org.jdesktop.swingx.JXDatePicker();
        searchDateToLabel = new javax.swing.JLabel();
        toDatePicker = new org.jdesktop.swingx.JXDatePicker();
        searchCategoryLabel = new javax.swing.JLabel();
        categoryFilterComboBox = new javax.swing.JComboBox<>();
        searchInStockLabel = new javax.swing.JLabel();
        inStockCheckBox = new javax.swing.JCheckBox();
        searchButton = new javax.swing.JButton();
        colorFilterComboBox = new javax.swing.JComboBox<>();
        navigationPanel = new javax.swing.JPanel();
        pageLabel = new javax.swing.JLabel();
        previousPageButton = new javax.swing.JButton();
        pagesTextField = new javax.swing.JTextField();
        nextPageButton = new javax.swing.JButton();
        ofPagesLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        viewResultsLabel = new javax.swing.JLabel();
        itemsPerPageComboBox = new javax.swing.JComboBox<>();
        perPageLabel = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        totalRecordsLabel = new javax.swing.JLabel();

        addProductDialog.setAlwaysOnTop(true);
        addProductDialog.setMinimumSize(new java.awt.Dimension(450, 340));
        addProductDialog.setPreferredSize(new java.awt.Dimension(450, 340));

        jPanel1.setMinimumSize(new java.awt.Dimension(450, 290));
        jPanel1.setPreferredSize(new java.awt.Dimension(390, 280));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        nameLabel.setText("Name");
        nameLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        jPanel1.add(nameLabel, gridBagConstraints);

        priceLabel.setText("Price");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        jPanel1.add(priceLabel, gridBagConstraints);

        colorLabel.setText("Color");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 20);
        jPanel1.add(colorLabel, gridBagConstraints);

        inStockLabel.setText("In Stock");
        inStockLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 20);
        jPanel1.add(inStockLabel, gridBagConstraints);

        expirationDateLabel.setText("Expiring date");
        expirationDateLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 20);
        jPanel1.add(expirationDateLabel, gridBagConstraints);

        categoryLabel.setText("Category");
        categoryLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 20);
        jPanel1.add(categoryLabel, gridBagConstraints);

        nameTextField.setPreferredSize(new java.awt.Dimension(155, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 60;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel1.add(nameTextField, gridBagConstraints);

        priceTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 60;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel1.add(priceTextField, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel1.add(setInStockCheckBox, gridBagConstraints);

        buttonsPanel.setLayout(new java.awt.GridBagLayout());

        okAddProductButton.setText("OK");
        okAddProductButton.setMaximumSize(new java.awt.Dimension(122, 32));
        okAddProductButton.setMinimumSize(new java.awt.Dimension(122, 32));
        okAddProductButton.setPreferredSize(new java.awt.Dimension(122, 32));
        okAddProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okAddProductButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 0, 0);
        buttonsPanel.add(okAddProductButton, gridBagConstraints);

        cancelAddProductButton.setText("Cancel");
        cancelAddProductButton.setMaximumSize(new java.awt.Dimension(122, 32));
        cancelAddProductButton.setMinimumSize(new java.awt.Dimension(122, 32));
        cancelAddProductButton.setPreferredSize(new java.awt.Dimension(122, 32));
        cancelAddProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelAddProductButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        buttonsPanel.add(cancelAddProductButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel1.add(buttonsPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel1.add(setColorComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel1.add(setCategoryComboBox, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel1.add(setExpiringDatePicker, gridBagConstraints);

        javax.swing.GroupLayout addProductDialogLayout = new javax.swing.GroupLayout(addProductDialog.getContentPane());
        addProductDialog.getContentPane().setLayout(addProductDialogLayout);
        addProductDialogLayout.setHorizontalGroup(
            addProductDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addProductDialogLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                .addContainerGap())
        );
        addProductDialogLayout.setVerticalGroup(
            addProductDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addProductDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1000, 600));

        tableTitleLabel.setText("Products Catalog");

        jScrollPane1.setMinimumSize(new java.awt.Dimension(20, 25));

        productsCatalogTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane1.setViewportView(productsCatalogTable);

        mainWindowButtonsPanel.setLayout(new java.awt.GridBagLayout());

        addProductButton.setText("Add Product");
        addProductButton.setToolTipText("Add a new product or edit an existing one");
        addProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addProductButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        mainWindowButtonsPanel.add(addProductButton, gridBagConstraints);

        modifyProductButton.setText("Modify Product");
        modifyProductButton.setToolTipText("Restore table to original value");
        modifyProductButton.setMaximumSize(new java.awt.Dimension(122, 32));
        modifyProductButton.setMinimumSize(new java.awt.Dimension(122, 32));
        modifyProductButton.setPreferredSize(new java.awt.Dimension(122, 32));
        modifyProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyProductButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        mainWindowButtonsPanel.add(modifyProductButton, gridBagConstraints);

        deleteProductButton.setText("Delete Product");
        deleteProductButton.setToolTipText("Delete selected product");
        deleteProductButton.setMaximumSize(new java.awt.Dimension(122, 32));
        deleteProductButton.setMinimumSize(new java.awt.Dimension(122, 32));
        deleteProductButton.setPreferredSize(new java.awt.Dimension(122, 32));
        deleteProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteProductButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        mainWindowButtonsPanel.add(deleteProductButton, gridBagConstraints);

        exitApplicationButton.setText("Exit");
        exitApplicationButton.setToolTipText("Exit Phone Book");
        exitApplicationButton.setMaximumSize(new java.awt.Dimension(122, 32));
        exitApplicationButton.setMinimumSize(new java.awt.Dimension(122, 32));
        exitApplicationButton.setPreferredSize(new java.awt.Dimension(122, 32));
        exitApplicationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitApplicationButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        mainWindowButtonsPanel.add(exitApplicationButton, gridBagConstraints);

        unsortTableButton.setText("Unsort Table");
        unsortTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unsortTableButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        mainWindowButtonsPanel.add(unsortTableButton, gridBagConstraints);

        searchPanel.setLayout(new java.awt.GridBagLayout());

        searchNameLabel.setText("Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        searchPanel.add(searchNameLabel, gridBagConstraints);

        searchNameTextField.setSelectedTextColor(new java.awt.Color(51, 51, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        searchPanel.add(searchNameTextField, gridBagConstraints);

        searchPriceLabel.setText("Price");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        searchPanel.add(searchPriceLabel, gridBagConstraints);

        searchPriceFromTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 70;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        searchPanel.add(searchPriceFromTextField, gridBagConstraints);

        searchPriceToLabel.setText("to");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        searchPanel.add(searchPriceToLabel, gridBagConstraints);

        searchPriceToTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 70;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        searchPanel.add(searchPriceToTextField, gridBagConstraints);

        searchColorLabel.setText("Color");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        searchPanel.add(searchColorLabel, gridBagConstraints);

        searchDateLabel.setText("Date");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        searchPanel.add(searchDateLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        searchPanel.add(fromDatePicker, gridBagConstraints);

        searchDateToLabel.setText("to");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        searchPanel.add(searchDateToLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        searchPanel.add(toDatePicker, gridBagConstraints);

        searchCategoryLabel.setText("Category");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        searchPanel.add(searchCategoryLabel, gridBagConstraints);

        categoryFilterComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-ANY-" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        searchPanel.add(categoryFilterComboBox, gridBagConstraints);

        searchInStockLabel.setText("In stock");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        searchPanel.add(searchInStockLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        searchPanel.add(inStockCheckBox, gridBagConstraints);

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        searchPanel.add(searchButton, gridBagConstraints);

        colorFilterComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-ANY-" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        searchPanel.add(colorFilterComboBox, gridBagConstraints);

        navigationPanel.setLayout(new java.awt.GridBagLayout());

        pageLabel.setText("Page");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        navigationPanel.add(pageLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        navigationPanel.add(previousPageButton, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        navigationPanel.add(pagesTextField, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        navigationPanel.add(nextPageButton, gridBagConstraints);

        ofPagesLabel.setText("of 1 pages");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        navigationPanel.add(ofPagesLabel, gridBagConstraints);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        navigationPanel.add(jSeparator1, gridBagConstraints);

        viewResultsLabel.setText("View");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        navigationPanel.add(viewResultsLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        navigationPanel.add(itemsPerPageComboBox, gridBagConstraints);

        perPageLabel.setText("per page");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        navigationPanel.add(perPageLabel, gridBagConstraints);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        navigationPanel.add(jSeparator2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        navigationPanel.add(totalRecordsLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        searchPanel.add(navigationPanel, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tableTitleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(mainWindowButtonsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tableTitleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainWindowButtonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okAddProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okAddProductButtonActionPerformed
        int id = productsCatalogTable.getSelectedRow();
        if (id == -1) {
            confirmAddProduct();
        } else {
            confirmModifyProduct(createProductFromFieldsEntry(), getProductInfo().getId());
        }
    }//GEN-LAST:event_okAddProductButtonActionPerformed

    private void cancelAddProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelAddProductButtonActionPerformed
        emptyFields();
        addProductDialog.dispose();
    }//GEN-LAST:event_cancelAddProductButtonActionPerformed

    private void addProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addProductButtonActionPerformed
        displayAddProductForm();
    }//GEN-LAST:event_addProductButtonActionPerformed

    private void exitApplicationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitApplicationButtonActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitApplicationButtonActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        searchProduct();
    }//GEN-LAST:event_searchButtonActionPerformed

    private void deleteProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteProductButtonActionPerformed
        deleteProducts();
        refreshJTable();
    }//GEN-LAST:event_deleteProductButtonActionPerformed

    private void modifyProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyProductButtonActionPerformed
        fillModifyProductFields(getProductInfo());
    }//GEN-LAST:event_modifyProductButtonActionPerformed

    private void unsortTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unsortTableButtonActionPerformed
        productsCatalogTable.setRowSorter(null);
        jScrollPane1.setViewportView(productsCatalogTable);
    }//GEN-LAST:event_unsortTableButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addProductButton;
    private javax.swing.JDialog addProductDialog;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton cancelAddProductButton;
    private javax.swing.JComboBox<String> categoryFilterComboBox;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JComboBox<String> colorFilterComboBox;
    private javax.swing.JLabel colorLabel;
    private javax.swing.JButton deleteProductButton;
    private javax.swing.JButton exitApplicationButton;
    private javax.swing.JLabel expirationDateLabel;
    private org.jdesktop.swingx.JXDatePicker fromDatePicker;
    private javax.swing.JCheckBox inStockCheckBox;
    private javax.swing.JLabel inStockLabel;
    private javax.swing.JComboBox<String> itemsPerPageComboBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPanel mainWindowButtonsPanel;
    private javax.swing.JButton modifyProductButton;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JPanel navigationPanel;
    private javax.swing.JButton nextPageButton;
    private javax.swing.JLabel ofPagesLabel;
    private javax.swing.JButton okAddProductButton;
    private javax.swing.JLabel pageLabel;
    private javax.swing.JTextField pagesTextField;
    private javax.swing.JLabel perPageLabel;
    private javax.swing.JButton previousPageButton;
    private javax.swing.JLabel priceLabel;
    private javax.swing.JFormattedTextField priceTextField;
    private javax.swing.JTable productsCatalogTable;
    private javax.swing.JButton searchButton;
    private javax.swing.JLabel searchCategoryLabel;
    private javax.swing.JLabel searchColorLabel;
    private javax.swing.JLabel searchDateLabel;
    private javax.swing.JLabel searchDateToLabel;
    private javax.swing.JLabel searchInStockLabel;
    private javax.swing.JLabel searchNameLabel;
    private javax.swing.JTextField searchNameTextField;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JFormattedTextField searchPriceFromTextField;
    private javax.swing.JLabel searchPriceLabel;
    private javax.swing.JLabel searchPriceToLabel;
    private javax.swing.JFormattedTextField searchPriceToTextField;
    private javax.swing.JComboBox<String> setCategoryComboBox;
    private javax.swing.JComboBox<String> setColorComboBox;
    private org.jdesktop.swingx.JXDatePicker setExpiringDatePicker;
    private javax.swing.JCheckBox setInStockCheckBox;
    private javax.swing.JLabel tableTitleLabel;
    private org.jdesktop.swingx.JXDatePicker toDatePicker;
    private javax.swing.JLabel totalRecordsLabel;
    private javax.swing.JButton unsortTableButton;
    private javax.swing.JLabel viewResultsLabel;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ProductCatalog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProductCatalog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProductCatalog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProductCatalog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProductCatalog().setVisible(true);
            }
        });
    }
}
