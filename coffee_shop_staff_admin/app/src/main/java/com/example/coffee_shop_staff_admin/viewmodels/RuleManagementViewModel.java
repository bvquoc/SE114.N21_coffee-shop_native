package com.example.coffee_shop_staff_admin.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.coffee_shop_staff_admin.BR;

public class RuleManagementViewModel extends BaseObservable {
    @Bindable
    private boolean loading = false;

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        notifyPropertyChanged(BR.loading);
    }

    @Bindable
    private boolean canFind = false;

    public boolean isCanFind() {
        return canFind;
    }

    public void setCanFind(boolean canFind) {
        this.canFind = canFind;
        notifyPropertyChanged(BR.canFind);
    }

    @Bindable
    private String email = "";

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    public void onEmailChanged(CharSequence s, int start, int before, int count)
    {
        email = s.toString();
    }

    @Bindable
    private String name = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }
    @Bindable
    private boolean active = true;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        notifyPropertyChanged(BR.active);
        if(active)
        {
            setNormalUserText("Chặn");
        }
        else
        {
            setNormalUserText("Bỏ chặn");
        }
    }

    @Bindable
    private String normalUserText = "Chặn";

    public String getNormalUserText() {
        return normalUserText;
    }

    public void setNormalUserText(String normalUserText) {
        this.normalUserText = normalUserText;
        notifyPropertyChanged(BR.normalUserText);
    }

    @Bindable
    private boolean staff = true;

    public boolean isStaff() {
        return staff;
    }

    public void setStaff(boolean staff) {
        this.staff = staff;
        notifyPropertyChanged(BR.staff);
        if(staff)
        {
            setStaffText("Bỏ quyền");
        }
        else
        {
            setStaffText("Cài quyền");
        }
    }
    @Bindable
    private String staffText = "Cài quyền";

    public String getStaffText() {
        return staffText;
    }

    public void setStaffText(String staffText) {
        this.staffText = staffText;
        notifyPropertyChanged(BR.staffText);
    }


    @Bindable
    private boolean admin = true;

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
        notifyPropertyChanged(BR.admin);
        if(admin)
        {
            setAdminText("Bỏ quyền");
        }
        else
        {
            setAdminText("Cài quyền");
        }
    }
    @Bindable
    private String adminText = "Cài quyền";

    public String getAdminText() {
        return adminText;
    }

    public void setAdminText(String adminText) {
        this.adminText = adminText;
        notifyPropertyChanged(BR.adminText);
    }



    @Bindable
    private boolean canEditAdminAccess = true;

    public boolean isCanEditAdminAccess() {
        return canEditAdminAccess;
    }

    public void setCanEditAdminAccess(boolean canEditAdminAccess) {
        this.canEditAdminAccess = canEditAdminAccess;
        notifyPropertyChanged(BR.canEditAdminAccess);
    }
}
