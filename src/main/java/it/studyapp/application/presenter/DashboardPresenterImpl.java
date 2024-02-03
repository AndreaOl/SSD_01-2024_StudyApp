package it.studyapp.application.presenter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.Series;

import it.studyapp.application.entity.Session;
import it.studyapp.application.entity.Student;
import it.studyapp.application.event.SessionRemovedEvent;
import it.studyapp.application.event.SessionRequestAcceptedEvent;
import it.studyapp.application.event.SessionUpdatedEvent;
import it.studyapp.application.security.SecurityService;
import it.studyapp.application.service.DataService;
import it.studyapp.application.view.DashboardView;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DashboardPresenterImpl implements DashboardPresenter {
	
	private DashboardView view;
	
	@Autowired
	private DataService dataService;
	
	@Autowired
	private SecurityService securityService;

	public DashboardPresenterImpl() {
		ComponentUtil.addListener(UI.getCurrent(), SessionRequestAcceptedEvent.class, e -> updateAllData());
		ComponentUtil.addListener(UI.getCurrent(), SessionUpdatedEvent.class, e -> updateAllData());
		ComponentUtil.addListener(UI.getCurrent(), SessionRemovedEvent.class, e -> updateAllData());
	}

	@Override
	public void setView(DashboardView view) {
		this.view = view;
	}

	@Override
	public void updateChart(YearMonth selectedYearMonth) {
		List<Series> newSeriesList = new ArrayList<>();
		List<Session> monthSessions;
		
		if(!securityService.isAdmin()) {
			Student thisStudent = dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0);
			
			monthSessions = thisStudent.getSessions().stream()
					.filter(s -> s.getDate().getMonthValue() == selectedYearMonth.getMonthValue())
					.collect(Collectors.toList());
		} else {
			monthSessions = dataService.findAllSessions().stream()
					.filter(s -> s.getDate().getMonthValue() == selectedYearMonth.getMonthValue())
					.collect(Collectors.toList());
		}
		
		int last_idx = selectedYearMonth.isValidDay(29) ? 4 : 3;
		
		if(monthSessions.isEmpty()) {
			ListSeries emptySeries = new ListSeries("No sessions in " + selectedYearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.US));
			for(int i = 0; i <= last_idx; ++i) emptySeries.addData(0);
			newSeriesList.add(emptySeries);
			
			view.setChartSeries(newSeriesList);
			view.drawChart();
			
			return;
		}
		
		Set<String> monthSubjects = monthSessions.stream()
				.map(Session::getSubject)
				.collect(Collectors.toSet());

		monthSubjects.forEach(subject -> {

			ListSeries subjectSeries = new ListSeries(subject);

			for(int i = 0; i <= last_idx; ++i) {

				LocalDate firstDayOfWeek = selectedYearMonth.atDay(i*7+1);
				
				LocalDate borderDate = i < 4 ? firstDayOfWeek.plusDays(7) : selectedYearMonth.atEndOfMonth().plusDays(1);

				List<Session> weekSessions = monthSessions.stream()
						.filter(s -> s.getSubject().equals(subject)
								&& (s.getDate().toLocalDate().isEqual(firstDayOfWeek) 
										|| (s.getDate().toLocalDate().isAfter(firstDayOfWeek) 
										&& s.getDate().toLocalDate().isBefore(borderDate))))
						.collect(Collectors.toList());

				subjectSeries.addData(weekSessions.size());
			}
			newSeriesList.add(subjectSeries);
		});
		
		view.setChartSeries(newSeriesList);
		view.drawChart();
	}

	@Override
	public void updateSessionGrid() {
		List<Session> lss;
		
		if(!securityService.isAdmin()) {
			Student thisStudent = dataService.searchStudent(securityService.getAuthenticatedUser().getUsername()).get(0);
		
			lss = thisStudent.getSessions();
		} else {
			lss = dataService.findAllSessions();
		}
		
		Collections.sort(lss, Comparator.comparing(Session::getDate));
		
		view.setSessionGridItems(lss);
	}
	
	@Override
	public boolean isAdmin() {
		return securityService.isAdmin();
	}

	private void updateAllData() {
		updateChart(view.getSelectedMonth());
		updateSessionGrid();
	}

}
