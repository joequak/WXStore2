/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package reportManagement;

import java.util.Date;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author mac
 */
@Named(value = "hitoricalReportMB")
@ViewScoped
public class hitoricalReportMB {

    private Date start;
    private Date end;
    
    public hitoricalReportMB() {
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
    
    // functions
    
    public String historicalReport(){
        if (start == null || end == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "!", "Please select starting date and end date"));
            return null;

        } else {
            System.out.println(start + " " + end);
            
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            Map<String, Object> sessionMap = externalContext.getSessionMap();
            sessionMap.put("StartDate", start);
            sessionMap.put("EndDate", end);
            
        }
        return "historicalReportResult.xhtml?faces-redirect=true";
    }
    
}
