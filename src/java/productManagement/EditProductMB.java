/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package productManagement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.bean.ViewScoped;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceRef;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import product.Categories;
import product.CategoryWS_Service;
import product.Comment;
import product.Product;
import product.ProductWS_Service;
import product.SubCategories;
import product.SubCategoryWS_Service;

/**
 *
 * @author mac
 */
@ManagedBean (name = "editProductMB")
@ViewScoped
public class EditProductMB {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/subCategoryWS.wsdl")
    private SubCategoryWS_Service service_2;
    
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/productWS.wsdl")
    private ProductWS_Service service_1;
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/categoryWS.wsdl")
    private CategoryWS_Service service;
    
    private Product myProduct;
    private Comment selectedComment;
    private UploadedFile file;
    private String fileUrl;
    private String productName;
    private String productDesc;
    private int availableQ;
    private double price;
    private double cost;
    private int discount;
    private String volume;
    private List<Comment> allComments;
    private double rate;
    private String allSub = "";
    private TreeNode root;
    private TreeNode selectedNode;
    private List<Categories> listOfCate = new ArrayList();
    private List<SubCategories> listOfSub = new ArrayList();
    private List<SubCategories> addedSub = new ArrayList();
    private Categories selectedCat;
    private SubCategories selectedSub;
    private List<SubCategories> tobeRemove;
    
    public EditProductMB() {
    }
    
    @PostConstruct
    public void init() {
        myProduct = (Product) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selectedProduct");
        listOfCate = this.viewAllCategories();
        root = new DefaultTreeNode("Root", null);
        for (Object o : listOfCate) {
            Categories myC = (Categories) o;
            root.getChildren().add(new DefaultTreeNode(myC.getName()));
            
        }
        allComments = myProduct.getCommentCollection();
        fileUrl = myProduct.getPicture();
        productName = myProduct.getName();
        productDesc = myProduct.getDescription();
        this.availableQ = myProduct.getAvailableQuantity();
        this.price = myProduct.getPrice();
        this.discount = myProduct.getDiscount();
        this.cost = myProduct.getCost();
        this.volume = myProduct.getVolumn();
        this.rate = myProduct.getAverageRate();
        tobeRemove = this.getProductAllSubCate(myProduct);
        for (Object o : tobeRemove) {
            SubCategories su = (SubCategories) o;
            allSub = allSub.concat(", " + su.getName());
        }
        
    }

    //MY FUNCTIONS
    public void handleFileUpload(FileUploadEvent event) throws IOException {
        
        this.setFile(event.getFile());
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String newFileName = "/Users/mac/Documents/school/WineXpress/WXStore/web/images/productImg/" + getFile().getFileName();
        fileUrl =  "./../images/productImg/" + getFile().getFileName();
        System.out.println("************************" + newFileName);
        System.err.println("newFileName: " + newFileName);
        try {
            FileOutputStream fos = new FileOutputStream(new File(newFileName));
            InputStream is = getFile().getInputstream();
            int BUFFER_SIZE = 8192;
            byte[] buffer = new byte[BUFFER_SIZE];
            int a;
            while (true) {
                a = is.read(buffer);
                if (a < 0) {
                    break;
                }
                fos.write(buffer, 0, a);
                fos.flush();
            }
            fos.close();
            is.close();
        } catch (IOException e) {
            FacesMessage msg = new FacesMessage("Invalid OpOrder Upload");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        FacesMessage message = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public void createNewProduct() {
        
        if (fileUrl.isEmpty() || productName.isEmpty() || price == 0 || cost == 0 || productDesc.isEmpty() || availableQ == 0 || discount == 0 || volume.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "! ", "Please"));
            
        } else {
            this.removeProductFromSubCate(tobeRemove, myProduct);
            this.editProduct(myProduct, fileUrl, productName, price, cost, productDesc, availableQ, discount, volume);
            for (Object o : addedSub) {
                SubCategories newSub = (SubCategories) o;
                this.addProductSubcategories(newSub, myProduct);
                
            }
            FacesMessage message = new FacesMessage("Succesful", "New Product Created");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        fileUrl = "";
        productName = "";
        price = 0;
        cost = 0;
        productDesc = "";
        availableQ = 0;
        discount = 0;
        volume = "";
    }
    
    public void addSubCategorie() {
        System.out.println("************" + selectedSub.getName());
        this.addedSub.add(selectedSub);
    }
    
    public void onNodeSelect(NodeSelectEvent event) {
        System.out.println("**********!!!!!!!!!!!!!___________selected ______________ " + this.selectedNode.toString());
        selectedCat = this.searchCategories(this.selectedNode.toString());
        listOfSub = selectedCat.getSubCategoriesCollection();
    }
    
    public void deleteProduct() throws IOException {
        this.deleteProduct_1(myProduct);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success ", "Product is Deleted "));
        FacesContext.getCurrentInstance().getExternalContext().redirect("../AdminPortal/viewAllProduct.xhtml");
        
    }

//GETTER AND SETTER
    public Product getMyProduct() {
        return myProduct;
    }
    
    public void setMyProduct(Product myProduct) {
        this.myProduct = myProduct;
    }
    
    public Comment getSelectedComment() {
        return selectedComment;
    }
    
    public void setSelectedComment(Comment selectedComment) {
        this.selectedComment = selectedComment;
    }
    
    public UploadedFile getFile() {
        return file;
    }
    
    public void setFile(UploadedFile file) {
        this.file = file;
    }
    
    public String getFileUrl() {
        return fileUrl;
    }
    
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductDesc() {
        return productDesc;
    }
    
    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }
    
    public int getAvailableQ() {
        return availableQ;
    }
    
    public void setAvailableQ(int availableQ) {
        this.availableQ = availableQ;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public double getCost() {
        return cost;
    }
    
    public void setCost(double cost) {
        this.cost = cost;
    }
    
    public int getDiscount() {
        return discount;
    }
    
    public void setDiscount(int discount) {
        this.discount = discount;
    }
    
    public String getVolume() {
        return volume;
    }
    
    public void setVolume(String volume) {
        this.volume = volume;
    }
    
    public List<Comment> getAllComments() {
        return allComments;
    }
    
    public void setAllComments(List<Comment> allComments) {
        this.allComments = allComments;
    }
    
    public double getRate() {
        return rate;
    }
    
    public void setRate(double rate) {
        this.rate = rate;
    }
    
    public String getAllSub() {
        return allSub;
    }
    
    public void setAllSub(String allSub) {
        this.allSub = allSub;
    }
    
    public TreeNode getRoot() {
        return root;
    }
    
    public void setRoot(TreeNode root) {
        this.root = root;
    }
    
    public TreeNode getSelectedNode() {
        return selectedNode;
    }
    
    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }
    
    public List<Categories> getListOfCate() {
        return listOfCate;
    }
    
    public void setListOfCate(List<Categories> listOfCate) {
        this.listOfCate = listOfCate;
    }
    
    public List<SubCategories> getListOfSub() {
        return listOfSub;
    }
    
    public void setListOfSub(List<SubCategories> listOfSub) {
        this.listOfSub = listOfSub;
    }
    
    public List<SubCategories> getAddedSub() {
        return addedSub;
    }
    
    public void setAddedSub(List<SubCategories> addedSub) {
        this.addedSub = addedSub;
    }
    
    public Categories getSelectedCat() {
        return selectedCat;
    }
    
    public void setSelectedCat(Categories selectedCat) {
        this.selectedCat = selectedCat;
    }
    
    public SubCategories getSelectedSub() {
        return selectedSub;
    }
    
    public void setSelectedSub(SubCategories selectedSub) {
        this.selectedSub = selectedSub;
    }

    //web service
    private java.util.List<product.Categories> viewAllCategories() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.CategoryWS port = service.getCategoryWSPort();
        return port.viewAllCategories();
    }
    
    private Categories searchCategories(java.lang.String categoriesName) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.CategoryWS port = service.getCategoryWSPort();
        return port.searchCategories(categoriesName);
    }
    
    private void addProductSubcategories(product.SubCategories subCat, product.Product myPro) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.ProductWS port = service_1.getProductWSPort();
        port.addProductSubcategories(subCat, myPro);
    }
    
    private Product saveNewProduct(java.lang.String picture, java.lang.String productName, double productPrice, double productCost, java.lang.String productDescription, int productAQ, int productDiscount, java.lang.String productVolume) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.ProductWS port = service_1.getProductWSPort();
        return port.saveNewProduct(picture, productName, productPrice, productCost, productDescription, productAQ, productDiscount, productVolume);
    }
    
    private boolean deleteProduct_1(product.Product productId) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.ProductWS port = service_1.getProductWSPort();
        return port.deleteProduct(productId);
    }
    
    private java.util.List<product.SubCategories> getProductAllSubCate(product.Product myProduct) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.SubCategoryWS port = service_2.getSubCategoryWSPort();
        return port.getProductAllSubCate(myProduct);
    }
    
    private void removeProductFromSubCate(java.util.List<product.SubCategories> listOfSub, product.Product myProduct) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.SubCategoryWS port = service_2.getSubCategoryWSPort();
        port.removeProductFromSubCate(listOfSub, myProduct);
    }

    private void editProduct(product.Product newProduct, java.lang.String picture, java.lang.String productName, double productPrice, double productCost, java.lang.String productDescription, int productAQ, int productDiscount, java.lang.String productVolume) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.ProductWS port = service_1.getProductWSPort();
        port.editProduct(newProduct, picture, productName, productPrice, productCost, productDescription, productAQ, productDiscount, productVolume);
    }
    
}
