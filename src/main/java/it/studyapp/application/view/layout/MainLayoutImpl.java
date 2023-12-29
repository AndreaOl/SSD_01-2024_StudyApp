package it.studyapp.application.view.layout;

import it.studyapp.application.event.NotificationsReadEvent;
import it.studyapp.application.presenter.layout.MainLayoutPresenter;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;

import it.studyapp.application.view.DashboardViewImpl;
import it.studyapp.application.view.calendar.CalendarViewImpl;
import it.studyapp.application.view.group.GroupViewImpl;
import it.studyapp.application.view.session.SessionViewImpl;

import java.util.Arrays;

import org.vaadin.lineawesome.LineAwesomeIcon;

public class MainLayoutImpl extends AppLayout implements MainLayout {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    
    private H2 viewTitle;
    private Span numberOfNotificationsSpan = new Span("0");
    private Avatar avatar = new Avatar();
    private SubMenu notificationsMenu;
    
    private MainLayoutPresenter presenter;
    
    public MainLayoutImpl(MainLayoutPresenter presenter) {
    	
        this.presenter = presenter;
        this.presenter.setView(this);
        this.presenter.registerUI();
        this.presenter.updateAvatar();
        
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();    
    }
    
    @Override
	public void setAvatarName(String username) {
    	avatar.setName(username);
	}

	@Override
	public void setAvatarImage(StreamResource imageResource) {
		avatar.setImageResource(imageResource);
	}

	@Override
	public void setNumberOfNotifications(int count) {
		numberOfNotificationsSpan.setText("" + count);		
	}

	@Override
	public void addNotification(HorizontalLayout notification) {
		notificationsMenu.addComponentAtIndex(0, notification);
	}

	@Override
	public void removeNotification(HorizontalLayout notification) {
		notificationsMenu.remove(notification);
	}

	@Override
    protected void onDetach(DetachEvent detachEvent) {
    	presenter.unregisterUI();
    	super.onDetach(detachEvent);
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        menuBar.setOpenOnHover(false);
        
        
        numberOfNotificationsSpan.getElement()
                .getThemeList()
                .addAll(Arrays.asList("badge", "error", "primary", "small", "pill"));
        numberOfNotificationsSpan.getStyle()
                .set("position", "absolute")
                .set("transform", "translate(-40%, -85%)");
        
        Button bellBtn = new Button(VaadinIcon.BELL_O.create());
        bellBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        bellBtn.getElement().appendChild(numberOfNotificationsSpan.getElement());

        MenuItem notificationsBell = menuBar.addItem(bellBtn);
        notificationsBell.getStyle().set("margin-right", "10px");
        notificationsBell.addClickListener(click -> {
        	ComponentUtil.fireEvent(UI.getCurrent(), new NotificationsReadEvent(UI.getCurrent(), false));
        });
        notificationsMenu = notificationsBell.getSubMenu();
        
        presenter.createNotifications();
        
        MenuItem menuItem = menuBar.addItem(avatar);
        SubMenu subMenu = menuItem.getSubMenu();
        subMenu.addItem("Home", e -> getUI().ifPresent(ui -> ui.navigate("")));
        subMenu.addItem("Profile", e -> getUI().ifPresent(ui -> ui.navigate("profile/me")));
        subMenu.addItem("Log out", e -> presenter.logout());

        
        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        HorizontalLayout header = new HorizontalLayout(toggle, viewTitle, menuBar);
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        header.expand(viewTitle); // <4>
        header.setWidthFull();
        header.addClassNames(
            LumoUtility.Padding.Vertical.NONE,
            LumoUtility.Padding.Horizontal.MEDIUM); 
        
        addToNavbar(true, header);
    }

    private void addDrawerContent() {
        Image logo = new Image("images/Logo.png", "Studyapp");
        logo.addClassName("studyapp-logo");

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(logo, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Dashboard", DashboardViewImpl.class, LineAwesomeIcon.HOME_SOLID.create()));
        nav.addItem(new SideNavItem("Calendar", CalendarViewImpl.class, LineAwesomeIcon.CALENDAR_ALT_SOLID.create()));
        nav.addItem(new SideNavItem("Groups", GroupViewImpl.class, LineAwesomeIcon.USER_FRIENDS_SOLID.create()));
        nav.addItem(new SideNavItem("Sessions", SessionViewImpl.class, LineAwesomeIcon.BUSINESS_TIME_SOLID.create()));
        
        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

}