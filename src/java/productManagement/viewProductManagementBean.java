/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package productManagement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.xml.ws.WebServiceRef;
import org.primefaces.event.RowEditEvent;
import product.Customer;
import product.OrderDetail;
import product.OrderItem;
import product.Product;
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
    private int quantity=1 ;//temporary
    private Collection<OrderItem> myshoppingcartList = new ArrayList<OrderItem>();
    private List<OrderItem> selectItems;
    private OrderDetail orderDetail;
    private List<OrderItem> orderDetails;
   
    private Double finalCost;
    
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public List<OrderItem> getSelectItems() {
        return selectItems;
    }

    public void setSelectItems(List<OrderItem> selectItems) {
        this.selectItems = selectItems;
    }

    public Double getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(Double finalCost) {
        this.finalCost = finalCost;
    }

    public OrderDetail getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }

    public List<OrderItem> getOrderDetails() {
        
        return getCustomerLatestOrderDetail(this.onCus);
       
    }

    public void setOrderDetails(List<OrderItem> orderDetails) {
        this.orderDetails = orderDetails;
    }

  
   

   
   

    
    
    public void oncancel() {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cannot Cancel your rate", "!");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    
      public void addToShoppingCart(javax.faces.event.ActionEvent event) {
          System.out.println("onCus&&&&"+onCus);   
           if ( onCus== null) {
            FacesMessage msg;
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid", "Login first/Register first");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            Product product1 = (Product) event.getComponent().getAttributes().get("product");
            System.out.println("Product&&&&&&&&&&:"+product1.getName());         
            addOrderItemAndShoppingCart(onCus,product1,quantity);
     }
    }
        public String shoppingfirst() {
       // String userName1 = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userName");
        if (onCus== null) {
            return "homePage";//register page

        } else {
            return "my-shoppingcart.xhtml?faces-redirect=true";
        }
    }
         public String orderDetail() {
       // String userName1 = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userName");
        if (onCus== null) {
            return "homePage";//register page

        } else {
            return "checkoutpage.xhtml?faces-redirect=true";
        }
    }

    public Collection<OrderItem> getMyshoppingcartList() {
       //  String userName1 = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userName");
       // Customer customer = customerSB.getCustomer(userName1);
        if (onCus == null) {
            FacesMessage msg;
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Dear", "Login first/Register first");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            myshoppingcartList.clear();
            return myshoppingcartList;
        } else {
            myshoppingcartList.clear();
            myshoppingcartList = getShoppingCartList(onCus);
            return myshoppingcartList;
        }
        
    }

    public void setMyshoppingcartList(Collection<OrderItem> myshoppingcartList) {
        this.myshoppingcartList = myshoppingcartList;
    }
    
     public void onRowEditProduct(RowEditEvent event) {

        OrderItem c = (OrderItem) event.getObject();
        System.out.println(">???????"+c.getQuantity());
         updateOrderItemQuantity(c);

       

        FacesMessage msg = new FacesMessage("Edit successfully");
        FacesContext.getCurrentInstance().addMessage(null, msg);

        // FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowCancelProduct(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled","" );
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
   
    public void totalCost(ActionEvent event){
      
        finalCost = calculateFinalCost(this.getSelectItems());
         ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
         Map<String, Object> sessionMap = externalContext.getSessionMap(); 
         sessionMap.put("orderPrice", finalCost);
    }
    
    public void generateOrderDetail(ActionEvent event){

         System.out.println("test111111!!!!!!");
        orderDetail = createOrderDetail(this.getSelectItems(), this.onCus);
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        sessionMap.put("orderID", orderDetail.getId());
        
    }
   
    public void deleteSelectOrderList(ActionEvent event){
        System.out.println("test222222!!!!");
        deleteOrderList(this.getSelectItems(),this.onCus);
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

    private long addOrderItemAndShoppingCart(product.Customer customer, product.Product product, int quantity) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.ProductWS port = service.getProductWSPort();
        return port.addOrderItemAndShoppingCart(customer, product, quantity);
    }

    private java.util.List<product.OrderItem> getShoppingCartList(product.Customer customer) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.ProductWS port = service.getProductWSPort();
        return port.getShoppingCartList(customer);
    }

    private void updateOrderItemQuantity(product.OrderItem orderItem) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.ProductWS port = service.getProductWSPort();
        port.updateOrderItemQuantity(orderItem);
    }

    private Double calculateFinalCost(java.util.List<product.OrderItem> selectedItems) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.ProductWS port = service.getProductWSPort();
        return port.calculateFinalCost(selectedItems);
    }

    private OrderDetail createOrderDetail(java.util.List<product.OrderItem> selectedItems, product.Customer customer) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.ProductWS port = service.getProductWSPort();
        return port.createOrderDetail(selectedItems, customer);
    }

    private void deleteOrderList(java.util.List<product.OrderItem> orderItems, product.Customer customer) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.ProductWS port = service.getProductWSPort();
        port.deleteOrderList(orderItems, customer);
    }

    private java.util.List<product.OrderItem> getCustomerLatestOrderDetail(product.Customer customer) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.ProductWS port = service.getProductWSPort();
        return port.getCustomerLatestOrderDetail(customer);
    }

   

}
