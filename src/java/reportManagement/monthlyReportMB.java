/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reportManagement;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.xml.ws.WebServiceRef;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;
import ws.OrderDetail;
import ws.ReportWS_Service;

/**
 *
 * @author mac
 */
@Named(value = "monthlyReportMB")
@ViewScoped
public class monthlyReportMB {
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/ReportWS.wsdl")
    private ReportWS_Service service_1;

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/ReportWS.wsdl")
    private ReportWS_Service service;
    private Date startDate;
    private Date endDate;
    private Date start2;
    private Date end2;
    private List<OrderDetail> currentOrderList;
    private List<OrderDetail> dayOrderList;
    private List<OrderDetail> orderList1;
    private List<OrderDetail> orderList2;

    private BarChartModel barModel;
    private LineChartModel dateModel = new LineChartModel();
    private LineChartModel lineModel2;
    private Double min = 1000.0;
    private Double max = 50.0;

    public monthlyReportMB() {

    }

    @PostConstruct
    public void init() {

        createBarModel();
        System.out.println("***************************init");
        createMyModels();
    }

    public void allValidOrder() {

    }

    //Getter and setter
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStart2() {
        return start2;
    }

    public void setStart2(Date start2) {
        this.start2 = start2;
    }

    public Date getEnd2() {
        return end2;
    }

    public void setEnd2(Date end2) {
        this.end2 = end2;
    }

    public ReportWS_Service getService() {
        return service;
    }

    public void setService(ReportWS_Service service) {
        this.service = service;
    }

    public ReportWS_Service getService_1() {
        return service_1;
    }

    public void setService_1(ReportWS_Service service_1) {
        this.service_1 = service_1;
    }

    public List<OrderDetail> getCurrentOrderList() {
        return currentOrderList;
    }

    public void setCurrentOrderList(List<OrderDetail> currentOrderList) {
        this.currentOrderList = currentOrderList;
    }

    public List<OrderDetail> getDayOrderList() {
        return dayOrderList;
    }

    public void setDayOrderList(List<OrderDetail> dayOrderList) {
        this.dayOrderList = dayOrderList;
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


    public BarChartModel getBarModel() {
        return barModel;
    }

    public void setBarModel(BarChartModel barModel) {
        this.barModel = barModel;
    }

    public List<OrderDetail> getOrderList1() {
        return orderList1;
    }

    public void setOrderList1(List<OrderDetail> orderList1) {
        this.orderList1 = orderList1;
    }

    public List<OrderDetail> getOrderList2() {
        return orderList2;
    }

    public void setOrderList2(List<OrderDetail> orderList2) {
        this.orderList2 = orderList2;
    }

    //Functions
    private void createMyModels() {
        System.out.println("***************************mymodels");
        lineModel2 = initMyModel();

        lineModel2.setTitle("Current Month Revenue Vs. Profit");
        lineModel2.setLegendPosition("e");
        lineModel2.setShowPointLabels(true);
        lineModel2.getAxes().put(AxisType.X, new CategoryAxis("Date"));
        Axis yAxis = lineModel2.getAxis(AxisType.Y);
        yAxis.setLabel("Value $");
        yAxis.setMin(min - 100);
        yAxis.setMax(max + 100);
    }

    private LineChartModel initMyModel() {
        System.out.println("***************************init models");
        Double totalValue;
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        int currentMonth = cal.get(Calendar.MONTH);
        int currentYear = cal.get(Calendar.YEAR);
        int currentTotalDays = this.getTotalNumberOfDays(currentMonth, currentYear);

        LineChartModel model = new LineChartModel();
        ChartSeries series1 = new ChartSeries();
        ChartSeries series2 = new ChartSeries();

        currentOrderList = this.orderOfTheMonth();

        for (int i = 0; i <= cal.get(Calendar.DAY_OF_MONTH); i++) {
            int day = i + 1;
            this.dayOrderList = this.orderOfTheDay(currentOrderList, day);
            totalValue = this.totalValue(dayOrderList);
            if (totalValue < min) {
                min = totalValue;
            }
            if (totalValue > max) {
                max = totalValue;
            }
            String xPoint = Integer.toString(day);
            series1.set(xPoint, totalValue);
        }
        for (int i = 0; i <= cal.get(Calendar.DAY_OF_MONTH); i++) {
            int day = i + 1;
            this.dayOrderList = this.orderOfTheDay(currentOrderList, day);
            totalValue = this.totalProfit(dayOrderList);
            if (totalValue < min) {
                min = totalValue;
            }
            if (totalValue > max) {
                max = totalValue;
            }
            String xPoint = Integer.toString(day);
            series2.set(xPoint, totalValue);
        }

        series1.setLabel("Revenue");
        series2.setLabel("Profit");

        model.addSeries(series1);
        model.addSeries(series2);

        return model;
    }

    private BarChartModel initBarModel() {
        System.out.println("***************************init bar model");
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        int currentYear = cal.get(Calendar.YEAR);
        System.out.println("****************currentYear" + currentYear);
        this.currentOrderList = this.orderOfYear(currentYear);
        System.out.println("****************after current year)000000000000" );
        this.orderList1 = this.orderOfYear(currentYear - 1);
        this.orderList2 = this.orderOfYear(currentYear - 2);
        BarChartModel model = new BarChartModel();
        ChartSeries revenue = new ChartSeries();
        revenue.setLabel("Revenue");

        double r1 = this.totalValue(currentOrderList);
        double r2 = this.totalValue(orderList1);
        double r3 = this.totalValue(orderList2);
        revenue.set(currentYear - 2, r3);
        revenue.set(currentYear - 1, r2);
        revenue.set(currentYear, r1);
        double p1 = this.totalProfit(currentOrderList);
        double p2 = this.totalProfit(orderList1);
        double p3 = this.totalProfit(orderList2);
        ChartSeries profit = new ChartSeries();
        profit.setLabel("Profit");
        profit.set(currentYear - 2, p3);
        profit.set(currentYear - 1, p2);
        profit.set(currentYear, p1);

        model.addSeries(revenue);
        model.addSeries(profit);

        if (r1 >= r2) {
            if (r2 >= r3) {
                min = r3;
                max = r1;
            }
            if (r2 <= r3) {
                min = r2;
                if (r3 >= r1) {
                    max = r3;
                } else {
                    max = r1;
                }

            }

        } else {
            if (r1 >= r3) {
                min = r3;
                max = r2;
            }
            if (r1 <= r3) {
                min = r1;
                if (r3 >= r2) {
                    max = r3;
                } else {
                    max = r2;
                }

            }
        }
        return model;
    }

    private void createBarModel() {
        System.out.println("***************************createBarModel");
        this.setBarModel(this.initBarModel());

        barModel.setTitle("Bar Chart");
        barModel.setLegendPosition("ne");

        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Year");

        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Value $");
        yAxis.setMin(min);
        yAxis.setMax(max);
    }

    //Wev Services

    private double getTotalValueOfMonth(java.util.List<ws.OrderDetail> orderList, int month, int year) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.getTotalValueOfMonth(orderList, month, year);
    }

    private double getTotalProfitOfMonth(java.util.List<ws.OrderDetail> orderList, int month, int year) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.getTotalProfitOfMonth(orderList, month, year);
    }

    private java.util.List<ws.OrderDetail> orderOfTheMonth() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.orderOfTheMonth();
    }

    private java.util.List<ws.OrderDetail> orderOfYear(int year) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.orderOfYear(year);
    }

    private int getTotalNumberOfDays(int month, int year) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.getTotalNumberOfDays(month, year);
    }

    private long retrieveTotalDay(javax.xml.datatype.XMLGregorianCalendar start, javax.xml.datatype.XMLGregorianCalendar end) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.retrieveTotalDay(start, end);
    }

    private java.util.List<ws.OrderDetail> orderOfTheDay(java.util.List<ws.OrderDetail> myorders, int day) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.orderOfTheDay(myorders, day);
    }

    private java.util.List<ws.OrderDetail> orderOfTheMonth_1() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.orderOfTheMonth();
    }

    private java.util.List<ws.OrderDetail> orderOfYear_1(int year) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.orderOfYear(year);
    }

    private java.util.List<ws.OrderDetail> orderOfTheMonth_2() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.orderOfTheMonth();
    }

    private long retrieveTotalDay_1(javax.xml.datatype.XMLGregorianCalendar start, javax.xml.datatype.XMLGregorianCalendar end) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.retrieveTotalDay(start, end);
    }

    private double totalValue(java.util.List<ws.OrderDetail> orderList) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.totalValue(orderList);
    }

    private double totalProfit(java.util.List<ws.OrderDetail> orderList) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.ReportWS port = service_1.getReportWSPort();
        return port.totalProfit(orderList);
    }
    

}
