package com.bt.om.entity.vo;

public class JqueryDataTable {
	private Object data;
    private int recordsTotal;
    private int recordsFiltered;

    public JqueryDataTable(Object data, int recordsTotal, int recordsFiltered) {
        super();
        this.data = data;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
    }

    public JqueryDataTable(Object data, int recordsTotal) {
        super();
        this.data = data;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsTotal;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(int recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public int getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(int recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }
}
