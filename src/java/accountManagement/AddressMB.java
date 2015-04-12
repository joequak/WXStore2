/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package accountManagement;

import java.io.IOException;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.xml.ws.WebServiceRef;
import ws.PaymentWS_Service;
import ws.ShipToAddress;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.faces.bean.ManagedProperty;
import product.Product;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import ws.CreditCard;

/**
 *
 * @author Ruoxi
 */
@ManagedBean(name = "addressMB")
@SessionScoped
public class AddressMB implements Serializable {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/PaymentWS.wsdl")
    private PaymentWS_Service service;

    /**
     * Creates a new instance of AddressMB
     */
    public AddressMB() {

    }
    //for encryption
    private static final char[] PASSWORD = "enfldsgbnlsngdlksdsgm".toCharArray();
    private static final byte[] SALT = {
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,};

    @ManagedProperty(value = "#{commonInfraMB.emailAdd}")
    private String email;

    @ManagedProperty(value = "#{viewProductManagementBean.orderID}")
    private long orderID;

    @ManagedProperty(value = "#{viewProductManagementBean.finalCost}")
    private double price;

    private ShipToAddress address;

    private String line1;
    private String line2;
    private String country;
    private String city;
    private String postal;
    private String phone;

    //For CreditCard
    private CreditCard credit;
    private String holderName;
    private String creditNum;
    private String exp;

    @PostConstruct
    public void Init() {

    }

    public void redirectAddress() {

        address = this.fetchAddress(getEmail());
        System.out.println("email is getting " + email);
        System.out.println("fetched address" + address.getShipState());
        if (address.getShipState().equals("0")) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("AddressNew.xhtml");
            } catch (IOException ex) {
            }
        }

        if (address.getShipState().equals("1")) {
            line1 = address.getAddress1();
            line2 = address.getAddress2();
            city = address.getCity();
            country = address.getCountry();
            phone = address.getContactNumber();
            postal = address.getPostalcode();

            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("AddressConfirm.xhtml");
            } catch (IOException ex) {
            }
        }
    }

    public void updateAddressMB() throws IOException, GeneralSecurityException {
        address.setAddress1(line1);
        address.setAddress2(line2);
        address.setCity(city);
        address.setContactNumber(phone);
        address.setCountry(country);
        address.setPostalcode(postal);
        //entered address before in database
        address.setShipState("1");

        this.updateAddress(getEmail(), address);

        credit = this.fetchCredit(getEmail());

        if (credit.getType().equals("0")) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("CreditNew.xhtml");
            } catch (IOException ex) {
            }
        }

        if (credit.getType().equals("1")) {
            this.creditNum = decrypt(credit.getCardNumber()).substring(0, 4);
            creditNum += "......";

            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("CreditConfirm.xhtml");
            } catch (IOException ex) {
            }
        }

        //Redirect to billing
    }

    public void updateCreditMB() throws IOException, GeneralSecurityException {
        credit.setCardHolder(encrypt(this.holderName));
        credit.setCardNumber(encrypt(this.creditNum));
        credit.setExpDate(encrypt(this.exp));

        this.updateCredit(getEmail(), credit);

        //Redirect to billing
    }

    public void confirm() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("AddressConfirm.xhtml");
        } catch (IOException ex) {
        }
    }

    public void enter() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("AddressNew.xhtml");
        } catch (IOException ex) {
        }
    }

    public void changeCredit() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("CreditNew.xhtml");
        } catch (IOException ex) {
        }
    }

    public void payment() throws IOException, GeneralSecurityException {

        System.out.println("printing order ID "+ getOrderID()  +"  price is   "+ getPrice());
        this.createPaymentRecord(orderID, price, "customer");
   
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("PayPal.xhtml");
        } catch (IOException ex) {
        }
    }

    public void clear() {
        this.setLine1("");
        this.setLine2("");
        this.setCity("");
        this.setCountry("");
        this.setPhone("");
        this.setPostal("");
        this.setCreditNum("");
        this.setExp("");
        this.setHolderName("");
    }

    private static String encrypt(String property) throws GeneralSecurityException, UnsupportedEncodingException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
    }

    private static String base64Encode(byte[] bytes) {
        // NB: This class is internal, and you probably should use another impl
        return new BASE64Encoder().encode(bytes);
    }

    private static String decrypt(String property) throws GeneralSecurityException, IOException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    private static byte[] base64Decode(String property) throws IOException {
        // NB: This class is internal, and you probably should use another impl
        return new BASE64Decoder().decodeBuffer(property);
    }

    /**
     * @return the address
     */
    public ShipToAddress getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(ShipToAddress address) {
        this.address = address;
    }

    /**
     * @return the line1
     */
    public String getLine1() {
        return line1;
    }

    /**
     * @param line1 the line1 to set
     */
    public void setLine1(String line1) {
        this.line1 = line1;
    }

    /**
     * @return the line2
     */
    public String getLine2() {
        return line2;
    }

    /**
     * @param line2 the line2 to set
     */
    public void setLine2(String line2) {
        this.line2 = line2;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the postal
     */
    public String getPostal() {
        return postal;
    }

    /**
     * @param postal the postal to set
     */
    public void setPostal(String postal) {
        this.postal = postal;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    private ShipToAddress fetchAddress(java.lang.String email) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.PaymentWS port = service.getPaymentWSPort();
        return port.fetchAddress(email);
    }

    private void updateAddress(java.lang.String email, ws.ShipToAddress newAddress) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.PaymentWS port = service.getPaymentWSPort();
        port.updateAddress(email, newAddress);
    }

    private CreditCard fetchCredit(java.lang.String email) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.PaymentWS port = service.getPaymentWSPort();
        return port.fetchCredit(email);
    }

    /**
     * @return the credit
     */
    public CreditCard getCredit() {
        return credit;
    }

    /**
     * @param credit the credit to set
     */
    public void setCredit(CreditCard credit) {
        this.credit = credit;
    }

    /**
     * @return the holderName
     */
    public String getHolderName() {
        return holderName;
    }

    /**
     * @param holderName the holderName to set
     */
    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    /**
     * @return the creditNum
     */
    public String getCreditNum() {
        return creditNum;
    }

    /**
     * @param creditNum the creditNum to set
     */
    public void setCreditNum(String creditNum) {
        this.creditNum = creditNum;
    }

    /**
     * @return the exp
     */
    public String getExp() {
        return exp;
    }

    /**
     * @param exp the exp to set
     */
    public void setExp(String exp) {
        this.exp = exp;
    }

    private void updateCredit(java.lang.String email, ws.CreditCard newCredit) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.PaymentWS port = service.getPaymentWSPort();
        port.updateCredit(email, newCredit);
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
     * @return the orderID
     */
    public long getOrderID() {
        return orderID;
    }

    /**
     * @param orderID the orderID to set
     */
    public void setOrderID(long orderID) {
        this.orderID = orderID;
    }

    /**
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }

    private void createPaymentRecord(long orderID, double price, java.lang.String name) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.PaymentWS port = service.getPaymentWSPort();
        port.createPaymentRecord(orderID, price, name);
    }


  

}
