package org.betaplus.beans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.betaplus.util.SimpleDataSource;

/**
 *
 * @author BenjaminAdamTaylor
 */
@ManagedBean
@RequestScoped
public class PdfBean {
    private Connection conn;
    private Statement stat;
    private HtmlDataTable dataTable;
    private List<PDFData> dataList = new LinkedList();
    private HtmlInputHidden dataItemId = new HtmlInputHidden();
    private PDFData dataItem = new PDFData();
    
    /**
     * Create a linked list containing all the data from the pdfs table
     * of the database starting from the most recent, 
     * sorted by the download date in descending order
     * @throws Exception 
     */
    private void loadDataList() throws Exception {
        getConnection();
        dataList.clear();
        ResultSet rs = stat.executeQuery("SELECT * FROM pdfs ORDER BY Dl_Date DESC");
        while(rs.next()) { 
            PDFData pdf = new PDFData();
            pdf.setPdfID(rs.getString("Pdf_Id"));
            pdf.setPdfHash(rs.getString("Pdf_Hash"));
            pdf.setDlDate(rs.getString("Dl_Date").substring(0, 10));
            pdf.setUrlID(rs.getString("Url_Id"));
            pdf.setPdfUrl(rs.getString("Pdf_Url"));
            pdf.setPdfName(rs.getString("Pdf_Name"));
            dataList.add(pdf);
        }
    }
    
    /**
     * Save the details of pdf object when it is selected in the UI datatable
     */
    public void selectDataItem() {
        // Obtain the row index from the hidden input element.
        String rowIndex = FacesContext.getCurrentInstance().getExternalContext()
            .getRequestParameterMap().get("rowIndex");
        if (rowIndex != null && rowIndex.trim().length() != 0) {
            int curIndex = dataTable.getFirst() + Integer.parseInt(rowIndex);
            dataItem = dataList.get(curIndex-1);
            dataItemId.setValue(dataItem.getPdfID());
        }
    }
    
    /**
     * Navigate to the front page of the datatable in the UI
     */
    public void pageFirst() {
        dataTable.setFirst(0);
    }

    /**
     * Navigate to the previous page of the datatable in the UI
     */
    public void pagePrevious() {
        dataTable.setFirst(dataTable.getFirst() - dataTable.getRows());
    }

    /**
     * Navigate to the next page of the datatable in the UI
     */
    public void pageNext() {
        dataTable.setFirst(dataTable.getFirst() + dataTable.getRows());
    }

    /**
     * Navigate to the last page of the datatable in the UI
     */
    public void pageLast() {
        int count = dataTable.getRowCount();
        int rows = dataTable.getRows();
        dataTable.setFirst(count - ((count % rows != 0) ? count % rows : rows));
    }
    
    /**
     * Get the current page of the datatable in the UI
     * @return 
     */
    public int getCurrentPage() {
        int rows = dataTable.getRows();
        int first = dataTable.getFirst();
        int count = dataTable.getRowCount();
        return (count / rows) - ((count - first) / rows) + 1;
    }

    /**
     * Get the total pages of the datatable in the UI
     * @return 
     */
    public int getTotalPages() {
        int rows = dataTable.getRows();
        int count = dataTable.getRowCount();
        return (count / rows) + ((count % rows != 0) ? 1 : 0);
    }
    
    /**
     * Get the list of pdf objects from the database
     * @return
     * @throws Exception 
     */
    public List<PDFData> getDataList() throws Exception {
        loadDataList();
        return dataList;
    }
    
    /**
     * Get the html datatable
     * @return 
     */
    public HtmlDataTable getDataTable() {
        return dataTable;
    }
    
    /**
     * Get the chosen pdf data object
     * @return 
     */
    public PDFData getDataItem() {
        return dataItem;
    }
    
    /**
     * Get the ID of the chosen pdf data object
     * @return 
     */
    public HtmlInputHidden getDataItemId() {
        return dataItemId;
    }
    
    /**
     * Set the html datatable
     * @param dataTable 
     */
    public void setDataTable(HtmlDataTable dataTable) {
        this.dataTable = dataTable;
    }
    
    /**
     * Set the currently selected data item
     * @param dataItem 
     */
    public void setDataItem(PDFData dataItem) {
        this.dataItem = dataItem;
    }
    
    /**
     * Set the currently selected data item id
     * @param dataItemId 
     */
    public void setDataItemId(HtmlInputHidden dataItemId) {
        this.dataItemId = dataItemId;
    }
    
    /**
     * Opens a connection to the database
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    private void getConnection() throws Exception
    {
        String fp = ((ServletContext) FacesContext.getCurrentInstance()
                .getExternalContext().getContext()).getRealPath("/");
        SimpleDataSource.init(fp + "data/database.properties");
        
        conn = SimpleDataSource.getConnection();
        stat = conn.createStatement();
    }
}
