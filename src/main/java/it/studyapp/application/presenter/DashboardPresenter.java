package it.studyapp.application.presenter;

import java.time.YearMonth;

import it.studyapp.application.view.DashboardView;

public interface DashboardPresenter {
	
	public void setView(DashboardView view);
	public void updateChart(YearMonth selectedYearMonth);
	public void updateSessionGrid();

}
