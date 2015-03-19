/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package productManagement;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.bean.ViewScoped;
import javax.xml.ws.WebServiceRef;
import product.Product;
import product.Customer;
import product.ProductWS_Service;
import product.SubCategories;
import product.SubCategoryWS_Service;
import wx.custAccMngmtWS.CustAccMngmtWS_Service;

/**
 *
 * @author mac
 */
@ManagedBean(name = "viewProductManagementBean")
@ViewScoped
public class viewProductManagementBean {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/subCategoryWS.wsdl")
    private SubCategoryWS_Service service_2;

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/CustAccMngmtWS.wsdl")
    private CustAccMngmtWS_Service service_1;

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/productWS.wsdl")
    private ProductWS_Service service;

    //Attributes
    private Product selectedProduct;
    private Integer rating;
    private String myComment;
    private Customer onCus;
    private String allSub="";
    @ManagedProperty(value = "#{commonInfraMB.logInCust}")
    private long logInCust;

    public viewProductManagementBean() {
    }

    @PostConstruct
    public void init() {
        selectedProduct = (Product) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("iselectedProduct");
        System.out.println("*******************selected Product"+selectedProduct.getName());
         System.out.println("**************************************"+logInCust);
        if (this.getLogInCust() != -1) {
            onCus = this.findCustomerById(this.getLogInCust());
        }
    }

    // geter and setter
    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public Integer getRating() {
        return rating;
    }

    public String getMyComment() {
        return myComment;
    }

    public long getLogInCust() {
        return logInCust;
    }

    public void setLogInCust(long logInCust) {
        this.logInCust = logInCust;
    }

    public void setMyComment(String myComment) {
        this.myComment = myComment;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Customer getOnCus() {
        onCus = this.findCustomerById(this.getLogInCust());
        return onCus;
    }

    public void setOnCus(Customer onCus) {
        this.onCus = onCus;
    }

    public void makeComment() {
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%!!!!"+this.myComment);
        this.makeComment_1(this.getSelectedProduct(), this.getMyComment(), this.getOnCus());
        myComment="";

    }

    public void makeRate() {
        this.rateProduct(this.getOnCus(), this.getSelectedProduct(), this.getRating());
    }

    public String getAllSub() {
        for (Object o : this.getProductAllSubCate(selectedProduct)) {
            SubCategories su = (SubCategories) o;
            allSub = allSub.concat(", " + su.getName());
        }
        return allSub;
    }

    public void setAllSub(String allSub) {
        this.allSub = allSub;
    }

    public void oncancel() {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cannot Cancel your rate", "!");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    //call web service
    private void rateProduct(product.Customer cus, product.Product myProduct, int myRate) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.ProductWS port = service.getProductWSPort();
        port.rateProduct(cus, myProduct, myRate);
    }

    private void makeComment_1(product.Product myProduct, java.lang.String newComment, product.Customer cus) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.ProductWS port = service.getProductWSPort();
        port.makeComment(myProduct, newComment, cus);
    }

    private Customer findCustomerById(long cusId) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.ProductWS port = service.getProductWSPort();
        return port.findCustomerById(cusId);
    }

    private java.util.List<product.SubCategories> getProductAllSubCate(product.Product myProduct) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.SubCategoryWS port = service_2.getSubCategoryWSPort();
        return port.getProductAllSubCate(myProduct);
    }

}
