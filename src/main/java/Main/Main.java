package Main;

import Controllers.CarCareDashboardController;
import Views.CarCareDashboard;


public class Main {
    public static void main(String[] args) {
        CarCareDashboard dashboard = new CarCareDashboard();
        new CarCareDashboardController(dashboard);
    }
}