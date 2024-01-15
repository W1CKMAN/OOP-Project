package Controllers;

import Views.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CarCareDashboardController {
    private CarCareDashboard dashboard;

    public CarCareDashboardController(CarCareDashboard dashboard) {
        this.dashboard = dashboard;

        this.dashboard.addOrderManagerButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OrderManagementView().setVisible(true);
            }
        });

        // this.dashboard.addJobsManagerButtonListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         new JobsManagerView().setVisible(true);
        //     }
        // });

        // this.dashboard.addcustomerDetailsManagerButtonListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         new CustomerDetailsManagerView().setVisible(true);
        //     }
        // });

        // this.dashboard.addSupplierManagerButtonListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         new SupplierManagerView().setVisible(true);
        //     }
        // });

        // this.dashboard.addInventoryManagerButtonListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         new InventoryManagerView().setVisible(true);
        //     }
        // });

        // this.dashboard.addEmployeeManagerButtonListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         new EmployeeManagerView().setVisible(true);
        //     }
        // });
    }
}