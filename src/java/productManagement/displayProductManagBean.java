/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package productManagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.bean.ViewScoped;
import javax.xml.ws.WebServiceRef;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import product.Categories;
import product.Product;
import product.ProductWS_Service;
import product.SubCategories;

/**
 *
 * @author mac
 */
@ManagedBean (name = "displayProductManagBean")
@ViewScoped
public class displayProductManagBean {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/productWS.wsdl")
    private ProductWS_Service service;

    private List<Product> allProducts = new ArrayList();
    private List<Categories> categories;
    private TreeNode root;
    private TreeNode selectedNode;
    private Product selectedProduct;

    /**
     * Creates a new instance of displayProductManagBean
     */
    public displayProductManagBean() {
    }

    @PostConstruct
    public void init() {
        //this.dataBaseInit();
         allProducts = this.viewAllProducts();
        categories = new ArrayList();
        categories = this.getAllCategories();
        root = new DefaultTreeNode("Root", null);
        for (Object o : categories) {
            Categories myC = (Categories) o;
            TreeNode fistNode = new DefaultTreeNode(myC.getName(), root);
            for (Object b : myC.getSubCategoriesCollection()) {
                SubCategories sub = (SubCategories) b;
                fistNode.getChildren().add(new DefaultTreeNode(sub.getName()));
                System.out.println("++++++++++++++++++++++++++++SubCate" + sub.getName());
            }
        }

    }

    public List<Product> getAllProducts() {
       
        return allProducts;
    }

    public void setAllProducts(List<Product> allProducts) {
        this.allProducts = allProducts;
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

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public void onNodeSelect(NodeSelectEvent event) {
        System.out.println("**********!!!!!!!!!!!!!___________selected ______________ "+this.selectedNode.toString());
        String categoryName = event.getTreeNode().toString();
        SubCategories searchSub = this.findSubCategoryByName(categoryName);
        System.out.println("**********!!!!!!!!!!!!!___________selected ______________ "+searchSub.getName()+searchSub.getId());
        allProducts= searchSub.getProductCollection();
        if(allProducts.isEmpty()){
            System.out.println("no product founded");
        }
    }

    public void onNodeUnselect(NodeUnselectEvent event) {
        this.setAllProducts(this.viewAllProducts());
    }

    public void onRowSelect(SelectEvent event) throws IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        System.out.println("*****^^^^^^^^^^^^!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+selectedProduct.getName());
        sessionMap.put("iselectedProduct", selectedProduct);
        FacesContext.getCurrentInstance().getExternalContext().redirect("../WineXpressStore/productDetail.xhtml");
    }
//**************************************web services**************************************

    private java.util.List<product.Product> viewAllProducts() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.ProductWS port = service.getProductWSPort();
        return port.viewAllProducts();
    }

    private java.util.List<product.Categories> getAllCategories() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.ProductWS port = service.getProductWSPort();
        return port.getAllCategories();
    }

    private SubCategories findSubCategoryByName(java.lang.String name) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.ProductWS port = service.getProductWSPort();
        return port.findSubCategoryByName(name);
    }

    public List<Categories> getCategories() {
        return categories;
    }

    public void setCategories(List<Categories> categories) {
        this.categories = categories;
    }



}
