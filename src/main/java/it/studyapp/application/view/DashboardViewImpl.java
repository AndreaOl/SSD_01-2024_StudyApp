package it.studyapp.application.view;

import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;

import it.studyapp.application.entity.Session;
import it.studyapp.application.presenter.DashboardPresenter;
import it.studyapp.application.security.Roles;
import it.studyapp.application.view.layout.MainLayoutImpl;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayoutImpl.class)
@RouteAlias(value = "", layout = MainLayoutImpl.class)
@RolesAllowed(Roles.USER)
public class DashboardViewImpl extends Main implements DashboardView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(DashboardViewImpl.class);
	
	private DashboardPresenter presenter;
	
	private Chart chart;
	private Grid<Session> sessionGrid;
	private Select<Month> monthSelection;

	public DashboardViewImpl(DashboardPresenter presenter) {
		this.presenter = presenter;
		this.presenter.setView(this);
		
		logger.info(UI.getCurrent() + ": Navigation to Dashboard page");
		
		addClassName("dashboard-view");
		setSizeFull();
		
		add(createChart());
		
		add(createSessionGrid());
		
		this.presenter.updateSessionGrid();
	}

	@Override
	public void setChartSeries(List<Series> series) {
		chart.getConfiguration().setSeries(series);
	}

	@Override
	public void drawChart() {
		chart.drawChart();
	}

	@Override
	public void setSessionGridItems(List<Session> items) {
		sessionGrid.setItems(items);
	}

	@Override
	public YearMonth getSelectedMonth() {
		return YearMonth.now().withMonth(monthSelection.getValue().getValue());
	}

	private Component createChart() {
		// Chart
		chart = new Chart(ChartType.COLUMN);
		Configuration conf = chart.getConfiguration();
		conf.getChart().setStyledMode(true);
		
		// Header
		monthSelection = new Select<>();
		monthSelection.setWidth("125px");
		monthSelection.setItems(Month.values());
		monthSelection.setItemLabelGenerator(item -> item.getDisplayName(TextStyle.FULL, Locale.US));
		monthSelection.addValueChangeListener(event -> {
			presenter.updateChart(YearMonth.now().withMonth(event.getValue().getValue()));
		});
		monthSelection.setValue(YearMonth.now().getMonth());

		HorizontalLayout header = createHeader("Session Summary", "Sessions/Week");
		header.add(monthSelection);		

		XAxis xAxis = new XAxis();
		xAxis.setCategories("Week 1", "Week 2", "Week 3", "Week 4", "Week 5");	
		conf.addxAxis(xAxis);
		conf.getyAxis().setTitle("Number of Sessions");

		PlotOptionsAreaspline plotOptions = new PlotOptionsAreaspline();
		plotOptions.setPointPlacement(PointPlacement.ON);
		plotOptions.setMarker(new Marker(false));
		conf.setPlotOptions(plotOptions);

		// Add it all together
		VerticalLayout viewEvents = new VerticalLayout(header, chart);
		viewEvents.addClassName(Padding.MEDIUM);
		viewEvents.setPadding(false);
		viewEvents.setSpacing(false);
		viewEvents.getElement().getThemeList().add("spacing-l");
		
		return viewEvents;
	}

	private Component createSessionGrid() {
		// Header
		HorizontalLayout header = createHeader("Sessions", "Scheduled for the next 7 days");

		// Grid
		sessionGrid = new Grid<>();
		sessionGrid.removeAllColumns();
		sessionGrid.addColumn(session -> session.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))).setHeader("Date");
		sessionGrid.addColumn(Session::getLocation).setHeader("Location");
		sessionGrid.addColumn(Session::getSubject).setHeader("Subject");
		sessionGrid.getColumns().forEach(col -> col.setAutoWidth(true));
		sessionGrid.setWidth("95%");
		sessionGrid.addItemClickListener(click -> {
			UI.getCurrent().navigate("sessions");
		});

		// Add it all together
		VerticalLayout dailyOrganization = new VerticalLayout(header, sessionGrid);
		dailyOrganization.setWidthFull();
		dailyOrganization.setHeight("70%");
		
		return dailyOrganization;
	}

	private HorizontalLayout createHeader(String title, String subtitle) {
		H2 h2 = new H2(title);
		h2.addClassNames(FontSize.XLARGE, Margin.NONE);

		Span span = new Span(subtitle);
		span.addClassNames(TextColor.SECONDARY, FontSize.XSMALL);

		VerticalLayout column = new VerticalLayout(h2, span);
		column.setPadding(false);
		column.setSpacing(false);

		HorizontalLayout header = new HorizontalLayout(column);
		header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		header.setSpacing(false);
		header.setWidthFull();
		
		return header;
	}

}
