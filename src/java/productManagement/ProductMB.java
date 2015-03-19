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

/**
 *
 * @author mac
 */
@ManagedBean (name = "productMB")
@ViewScoped
public class ProductMB {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/categoryWS.wsdl")
    private CategoryWS_Service service_1;

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/productWS.wsdl")
    private ProductWS_Service service;

    private SubCategories selectedSub;
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
    private List<Categories> listOfCate;
    private List<SubCategories> listOfSub = new ArrayList();
    private SubCategories previousSelection;
    private Categories selectedCat;
    private List<String> categoryName = new ArrayList();
    private TreeNode root;
    private TreeNode selectedNode;
    private List<SubCategories> addedSub = new ArrayList();

    /**
     * Creates a new instance of ProductMB
     */
    public ProductMB() {
    }

    @PostConstruct
    public void init() {
        listOfCate = this.viewAllCategories();
        root = new DefaultTreeNode("Root", null);
        for (Object o : listOfCate) {
            Categories myC = (Categories) o;
            root.getChildren().add(new DefaultTreeNode(myC.getName()));

        }

    }

    public void handleFileUpload(FileUploadEvent event) throws IOException {

        this.setFile(event.getFile());
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String newFileName = "/Users/mac/Documents/school/WineXpress/WXStore/web/images/productImg/" + getFile().getFileName();
        fileUrl = getFile().getFileName();
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
        System.out.println("************************Consent Form file upload ok?" + fileUrl);
        System.out.println("************************Consent Form file upload ok?" + this.productName);
        System.out.println("************************Consent Form file upload ok?" + this.productDesc);
        System.out.println("************************Consent Form file upload ok?" + this.price);

        System.out.println("************************Consent Form file upload ok?" + this.cost);
        System.out.println("************************Consent Form file upload ok?" + this.availableQ);

        if (fileUrl.isEmpty() || productName.isEmpty() || price == 0 || cost == 0 || productDesc.isEmpty() || availableQ == 0 || discount == 0 || volume.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "! ", "Please"));

        } else {
            Product newPro = this.saveNewProduct(fileUrl, productName, price, cost, productDesc, availableQ, discount, volume);
            for (Object o : addedSub) {
                SubCategories newSub = (SubCategories) o;
                this.addProductSubcategories(newSub, newPro);

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
        System.out.println("************"+selectedSub.getName());
        this.addedSub.add(selectedSub);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success ", "SubCategory added"));

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

    public SubCategories getSelectedSub() {
        return selectedSub;
    }

    public void setSelectedSub(SubCategories selectedSub) {
        this.selectedSub = selectedSub;
    }

    public Comment getSelectedComment() {
        return selectedComment;
    }

    public void setSelectedComment(Comment selectedComment) {
        this.selectedComment = selectedComment;
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

    public SubCategories getPreviousSelection() {
        return previousSelection;
    }

    public void setPreviousSelection(SubCategories previousSelection) {
        this.previousSelection = previousSelection;
    }

    public Categories getSelectedCat() {
        return selectedCat;
    }

    public void setSelectedCat(Categories selectedCat) {
        this.selectedCat = selectedCat;
    }

    public List<String> getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(List<String> categoryName) {
        this.categoryName = categoryName;
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

    public List<SubCategories> getAddedSub() {
        return addedSub;
    }

    public void setAddedSub(List<SubCategories> addedSub) {
        this.addedSub = addedSub;
    }

    
    public void onNodeSelect(NodeSelectEvent event) {
        System.out.println("**********!!!!!!!!!!!!!___________selected ______________ " + this.selectedNode.toString());
        selectedCat = this.searchCategories(this.selectedNode.toString());
        listOfSub = selectedCat.getSubCategoriesCollection();
    }

    //call web services
    private java.util.List<product.Categories> viewAllCategories() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.CategoryWS port = service_1.getCategoryWSPort();
        return port.viewAllCategories();
    }

    private Product saveNewProduct(java.lang.String picture, java.lang.String productName, double productPrice, double productCost, java.lang.String productDescription, int productAQ, int productDiscount, java.lang.String productVolume) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.ProductWS port = service.getProductWSPort();
        return port.saveNewProduct(picture, productName, productPrice, productCost, productDescription, productAQ, productDiscount, productVolume);
    }

    private Categories searchCategories(java.lang.String categoriesName) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.CategoryWS port = service_1.getCategoryWSPort();
        return port.searchCategories(categoriesName);
    }

    private void addProductSubcategories(product.SubCategories subCat, product.Product myPro) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.ProductWS port = service.getProductWSPort();
        port.addProductSubcategories(subCat, myPro);
    }

}
