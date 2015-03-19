/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custSupport;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.xml.ws.WebServiceRef;
import ws.CustEnquiry;
import ws.CustSupportWS_Service;

/**
 *
 * @author Ruoxi
 */
@ManagedBean(name = "custSupportMB")
@SessionScoped
public class CustSupportMB implements Serializable {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/CustSupportWS.wsdl")
    private CustSupportWS_Service service;

    /**
     * Creates a new instance of CustSupportMB
     */
    public CustSupportMB() {
    }

    @PostConstruct
    public void init() {

        setEnquiryList(getNewEnquiry());
        this.setEnquiryListReplied(this.getRepliedEnquiry());
    }

    private String email;
    private String subject;
    private String content;
    private List<CustEnquiry> enquiryList = new ArrayList<>();
    private CustEnquiry activeEnquiry;
    private String response;
    private List<CustEnquiry> enquiryListReplied = new ArrayList<>();

    public void askEnquiry() {
        CustEnquiry enquiry = new CustEnquiry();

        enquiry.setEmailAddress(email);
        enquiry.setSubject(subject);
        enquiry.setContent(content);
        content="";

        if (creatEnquiry(enquiry)) {
            //update table of enquiry
            setEnquiryList(getNewEnquiry());
            this.setEnquiryListReplied(this.getRepliedEnquiry());

            this.setEmail(null);
            this.setSubject(null);
            this.setContent(null);

            FacesMessage msg;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Enquiry Sent Successfully", "You have successfully created an enquiry, we will get back to you soon");
            FacesContext.getCurrentInstance().addMessage(null, msg);

        } else {
            FacesMessage msg;
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fail", "Failed to create enquiry, please try again later");
            FacesContext.getCurrentInstance().addMessage(null, msg);

        }

    }

    public void reply(CustEnquiry enquiry) {
        this.setActiveEnquiry(enquiry);

        setEnquiryList(getNewEnquiry());
        this.setEnquiryListReplied(this.getRepliedEnquiry());
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("CustEnquiryReply.xhtml");
        } catch (IOException ex) {

        }

    }

    public void view(CustEnquiry enquiry) {
        setEnquiryList(getNewEnquiry());
        this.setEnquiryListReplied(this.getRepliedEnquiry());
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("CustEnquiryViewReply.xhtml");
        } catch (IOException ex) {

        }

    }

    public void back() {

        setEnquiryList(getNewEnquiry());
        this.setEnquiryListReplied(this.getRepliedEnquiry());
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("CustEnquiryViewReplied.xhtml");
        } catch (IOException ex) {

        }

    }

    public void sendReply() {

        activeEnquiry.setReply(response);
        sendResponse(activeEnquiry.getId(), activeEnquiry.getReply());

        setEnquiryList(getNewEnquiry());
        this.setEnquiryListReplied(this.getRepliedEnquiry());

        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("CustEnquiryView.xhtml");
        } catch (IOException ex) {

        }

    }

    private boolean creatEnquiry(ws.CustEnquiry enquiry) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.CustSupportWS port = service.getCustSupportWSPort();
        return port.creatEnquiry(enquiry);
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the enquiryList
     */
    public List<CustEnquiry> getEnquiryList() {
        return enquiryList;
    }

    /**
     * @param enquiryList the enquiryList to set
     */
    public void setEnquiryList(List<CustEnquiry> enquiryList) {
        this.enquiryList = enquiryList;
    }

    /**
     * @return the activeEnquiry
     */
    public CustEnquiry getActiveEnquiry() {
        return activeEnquiry;
    }

    /**
     * @param activeEnquiry the activeEnquiry to set
     */
    public void setActiveEnquiry(CustEnquiry activeEnquiry) {
        this.activeEnquiry = activeEnquiry;
    }

    /**
     * @return the response
     */
    public String getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(String response) {
        this.response = response;
    }

    private boolean sendResponse(java.lang.Long id, java.lang.String response) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.CustSupportWS port = service.getCustSupportWSPort();
        return port.sendResponse(id, response);
    }

    private java.util.List<ws.CustEnquiry> getNewEnquiry() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.CustSupportWS port = service.getCustSupportWSPort();
        return port.getNewEnquiry();
    }

    /**
     * @return the enquiryListReplied
     */
    public List<CustEnquiry> getEnquiryListReplied() {
        return enquiryListReplied;
    }

    /**
     * @param enquiryListReplied the enquiryListReplied to set
     */
    public void setEnquiryListReplied(List<CustEnquiry> enquiryListReplied) {
        this.enquiryListReplied = enquiryListReplied;
    }

    private java.util.List<ws.CustEnquiry> getRepliedEnquiry() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.CustSupportWS port = service.getCustSupportWSPort();
        return port.getRepliedEnquiry();
    }

}
