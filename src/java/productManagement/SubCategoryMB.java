/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package productManagement;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.bean.ViewScoped;
import javax.xml.ws.WebServiceRef;
import product.Categories;
import product.SubCategories;
import product.SubCategoryWS_Service;

/**
 *
 * @author mac
 */
@ManagedBean (name = "subCategoryMB")
@ViewScoped
public class SubCategoryMB {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/subCategoryWS.wsdl")
    private SubCategoryWS_Service service;

    private SubCategories selectedSub;
    private Categories temp;
    private Categories myCategory;
    private String name;
    private List<SubCategories> listOfSub;

    public SubCategoryMB() {
    }

    @PostConstruct
    public void init() {
        temp = (Categories) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selectedCategory");
        myCategory = this.getCategoryById(temp.getId());
        //listOfSub=myCategory.getSubCategoriesCollection();
    }

    public void deleteSub() {
        this.deleteSubCategory(selectedSub);
    }

    public void addNewSub() {
        boolean result = true;
        result = this.addNewSubcategory(myCategory, name);
        name = "";
        if (result) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success ", "SubCategory added"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fail ", "SubCategory already exists"));

        }
    }

    //getter and setter
    public SubCategories getSelectedSub() {
        return selectedSub;
    }

    public void setSelectedSub(SubCategories selectedSub) {
        this.selectedSub = selectedSub;
    }

    public Categories getMyCategory() {
        myCategory = this.getCategoryById(temp.getId());
        return myCategory;
    }

    public void setMyCategory(Categories myCategory) {
        this.myCategory = myCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SubCategories> getListOfSub() {
        //listOfSub = this.getCategoryById(myCategory.getId()).getSubCategoriesCollection();
        listOfSub = this.getMyCategory().getSubCategoriesCollection();
        return listOfSub;
    }

    public void setListOfSub(List<SubCategories> listOfSub) {

        this.listOfSub = listOfSub;
    }

    //web services
    private boolean addNewSubcategory(product.Categories category, java.lang.String subName) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.SubCategoryWS port = service.getSubCategoryWSPort();
        return port.addNewSubcategory(category, subName);
    }

    private boolean deleteSubCategory(product.SubCategories mySub) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.SubCategoryWS port = service.getSubCategoryWSPort();
        return port.deleteSubCategory(mySub);
    }

    private product.Categories getCategoryById(long caId) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        product.SubCategoryWS port = service.getSubCategoryWSPort();
        return port.getCategoryById(caId);
    }

}
