package com.example.cafetariacompany2;
import android.os.Parcel;
import android.os.Parcelable;


public class Employee implements Parcelable {
    private String id;
    private String name;
    private String birthdate;
    private String phone;
    private String address;
    private String sex;
    private String avatar;
    public Employee() {
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getBirthdate() {
        return birthdate;
    }
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.birthdate);
        dest.writeString(this.phone);
        dest.writeString(this.address);
        dest.writeString(this.sex);
        dest.writeString(this.avatar);
    }
    protected Employee(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.birthdate = in.readString();
        this.phone = in.readString();
        this.address = in.readString();
        this.sex = in.readString();
        this.avatar = in.readString();
    }
    public static final Creator<Employee> CREATOR = new Creator<Employee>() {
        @Override
        public  Employee createFromParcel(Parcel source) {
            return new Employee(source);
        }
        @Override
        public Employee[] newArray(int size) { return new Employee[size];
        }
    };
}