/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reportManagement;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceRef;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;
import ws.OrderDetail;

import ws.ReportWS_Service;

/**
 *
 * @author mac
 */
@Named(value = "resultMB")
@ViewScoped
public class resultMB {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/ReportWS.wsdl")
    private ReportWS_Service service_1;

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/ReportWS.wsdl")
    private ReportWS_Service service;

    private Date start;
    private Date end;
    GregorianCalendar startDate;
    GregorianCalendar endDate;
    private List<OrderDetail> orderlist = new ArrayList();
    private LineChartModel dateModel = new LineChartModel();
    private LineChartModel lineModel2;
    private Double min = 50.0;
    private Double max = 50.0;
    private List<Report> dataList = new ArrayList();

    public resultMB() throws DatatypeConfigurationException {

    }

    @PostConstruct
    public void init() {
        start = (Date) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("StartDate");
        end = (Date) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("EndDate");
        startDate = new GregorianCalendar();
        endDate = new GregorianCalendar();
        startDate.setTime(start);
        endDate.setTime(end);
        createMyModels();

    }

    //getter and setter
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

    public List<OrderDetail> getOrderlist() {
        return orderlist;
    }

    public void setOrderlist(List<OrderDetail> orderlist) {
        this.orderlist = orderlist;
    }

    public LineChartModel getDateModel() {
        return dateModel;
    }

    public void setDateModel(LineChartModel dateModel) {
        this.dateModel = dateModel;
    }

    public LineChartModel getLineModel2() {
        return lineModel2;
    }

    public void setLineModel2(LineChartModel lineModel2) {
        this.lineModel2 = lineModel2;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public ReportWS_Service getService() {
        return service;
    }

    public void setService(ReportWS_Service service) {
        this.service = service;
    }

    public GregorianCalendar getStartDate() {
        return startDate;
    }

    public void setStartDate(GregorianCalendar startDate) {
        this.startDate = startDate;
    }

    public GregorianCalendar getEndDate() {
        return endDate;
    }

    public void setEndDate(GregorianCalendar endDate) {
        this.endDate = endDate;
    }

    public List<Report> getDataList() {
        return dataList;
    }

    public void setDataList(List<Report> dataList) {
        this.dataList = dataList;
    }

    //Fuctions
    private void createMyModels() {

        lineModel2 = initMyModel();

        lineModel2.setTitle("Revenue Vs. Profit");
        lineModel2.setLegendPosition("e");
        lineModel2.setShowPointLabels(true);
        lineModel2.getAxes().put(AxisType.X, new CategoryAxis("Date"));
        org.primefaces.model.chart.Axis yAxis = lineModel2.getAxis(AxisType.Y);
        yAxis.setLabel("Value $");
        yAxis.setMin(min - 100);
        yAxis.setMax(max + 100);
    }

    private LineChartModel initMyModel() {
        Double totalValue = 0.0;
        Double totalProfit = 0.0;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(start);
        cal2.setTime(end);
        int sday = cal1.get(Calendar.DAY_OF_MONTH);
        int smonth = cal1.get(Calendar.MONTH);
        int syear = cal1.get(Calendar.YEAR);
        int eday = cal1.get(Calendar.DAY_OF_MONTH);
        int emonth = cal1.get(Calendar.MONTH);
        int eyear = cal1.get(Calendar.YEAR);

        orderlist = this.orderWithinDates(sday, smonth, syear, eday, emonth, eyear);
        int totalMonth = this.retrieveTotalMonth(sday, smonth, syear, eday, emonth, eyear);
        LineChartModel model = new LineChartModel();
        ChartSeries series1 = new ChartSeries();
        ChartSeries series2 = new ChartSeries();
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        int startMonth = cal.get(Calendar.MONTH);
        startMonth++;
        int startYear = cal.get(Calendar.YEAR);
        Calendar cal11 = Calendar.getInstance();
        cal.setTime(end);
        int endMonth = cal11.get(Calendar.MONTH);
        endMonth++;
        int endYear = cal11.get(Calendar.YEAR);

        for (int i = 1; i <= totalMonth; i++) {
            totalProfit = this.getTotalProfitOfMonth(orderlist, startMonth, startYear);
            totalValue = this.getTotalValueOfMonth(orderlist, startMonth, startYear);
            if (totalValue < min) {
                min = totalValue;
            }
            if (totalValue > max) {
                max = totalValue;
            }
            String xPoint = Integer.toString(startYear) + "-" + Integer.toString(startMonth);
            series1.set(xPoint, totalValue);
            series2.set(xPoint, totalProfit);
            Report myReport = new Report();
            myReport.setMonth(xPoint);
            myReport.setProfit(totalProfit);
            myReport.setRevenue(totalValue);
            if (startMonth == 12) {
                startMonth = 1;
                startYear++;
            } else {
                startMonth++;
            }
        }

        series1.setLabel("Revenue");
        series2.setLabel("Profit");

        model.addSeries(series1);
        model.addSeries(series2);

        return model;
    }
    //WS

    private int getTotalNumberOfDays(int month, int year) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.getTotalNumberOfDays(month, year);
    }

    private java.util.List<ws.OrderDetail> orderOfTheMonth() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.orderOfTheMonth();
    }

    private java.util.List<ws.OrderDetail> orderOfTheDay(java.util.List<ws.OrderDetail> myorders, int day) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.orderOfTheDay(myorders, day);
    }

    private double getTotalValueofDay(java.util.List<ws.OrderDetail> orderList, int day) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.getTotalValueofDay(orderList, day);
    }

    private double getTotalProfitOfMonth(java.util.List<ws.OrderDetail> orderList, int month, int year) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.getTotalProfitOfMonth(orderList, month, year);
    }

    private double getTotalValueOfMonth(java.util.List<ws.OrderDetail> orderList, int month, int year) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.getTotalValueOfMonth(orderList, month, year);
    }

    private double getTotalProfitOfMonth_1(java.util.List<ws.OrderDetail> orderList, int month, int year) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.getTotalProfitOfMonth(orderList, month, year);
    }

    private int retrieveTotalMonth(int sday, int smonth, int syear, int eday, int emonth, int eyear) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.retrieveTotalMonth(sday, smonth, syear, eday, emonth, eyear);
    }

    private java.util.List<ws.OrderDetail> orderWithinDates(int sday, int smonth, int syear, int eday, int emonth, int eyear) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.orderWithinDates(sday, smonth, syear, eday, emonth, eyear);
    }

}
