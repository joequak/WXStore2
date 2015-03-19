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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.bean.ViewScoped;
import javax.servlet.ServletContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author mac
 */
@ManagedBean (name = "fileUploadMB")
@ViewScoped
public class FileUploadMB {

    private UploadedFile file;

    /**
     * Creates a new instance of FileUploadMB
     */
    public FileUploadMB() {
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException {
        System.out.println("************************1");

        System.out.println("************************2");

        this.setFile(event.getFile());
        System.out.println("************************2");
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String newFileName ="/Users/mac/Documents/school/WineXpress/WXStore/web/images/productImg/"+getFile().getFileName();
        //String newFileName = servletContext.getRealPath("") + File.separator + "images" + File.separator + "productImg" + File.separator + getFile().getFileName();
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
        System.out.println("************************Consent Form file upload ok?");
        FacesMessage message = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

}
