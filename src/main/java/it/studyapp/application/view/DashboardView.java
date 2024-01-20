package it.studyapp.application.view;

import java.time.YearMonth;
import java.util.List;


import com.vaadin.flow.component.charts.model.Series;

import it.studyapp.application.entity.Session;

public interface DashboardView {
	
	public void setChartSeries(List<Series> series);
	public void drawChart();
	public void setSessionGridItems(List<Session> items);
	public YearMonth getSelectedMonth();
	
}
