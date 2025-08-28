package com.aurionpro.bank_application.Models;

import com.aurionpro.bank_application.ENUMS.Roles;
import com.aurionpro.bank_application.ENUMS.STATUS;

import java.time.LocalDateTime;

public class User {
   private int id;
private int accountNumber;
private String username;
private String password;
private String email;
private String phone;
private String role;
private Long balance;
private LocalDateTime created_at;
private String status;
private boolean is_deleted;
    public User() {}
   public User(int accountNumber, String username, String password, String email, String phone, Roles role, long balance,
               LocalDateTime created_at, STATUS status, boolean is_deleted){
         this.accountNumber = accountNumber;
            this.username = username;
            this.password = password;
            this.email = email;
            this.phone = phone;
            this.role = role.name();
            this.balance = balance;
            this.created_at = created_at;
            this.status = status.name();
            this.is_deleted = is_deleted;
   }

    public User(int id, int accountNumber, String username, String password, String email, String phone, Roles role, long balance,
                LocalDateTime created_at, STATUS status, boolean is_deleted){
            this.id = id;
            this.accountNumber = accountNumber;
                this.username = username;
                this.password = password;
                this.email = email;
                this.phone = phone;
                this.role = role.name();
                this.balance = balance;
                this.created_at = created_at;
                this.status = status.name();
                this.is_deleted = is_deleted;
    }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public Integer getAccountNumber() {
      return accountNumber;
   }

   public void setAccountNumber(int accountNumber) {
      this.accountNumber = accountNumber;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getPhone() {
      return phone;
   }

   public void setPhone(String phone) {
      this.phone = phone;
   }

   public String getRole() {
      return role;
   }

   public void setRole(String role) {
      this.role = role;
   }

   public long getBalance() {
      return balance;
   }

   public void setBalance(long balance) {
      this.balance = balance;
   }

   public LocalDateTime getCreated_at() {
      return created_at;
   }

   public void setCreated_at(LocalDateTime created_at) {
      this.created_at = created_at;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public boolean isIs_deleted() {
      return is_deleted;
   }

   public void setIs_deleted(boolean is_deleted) {
      this.is_deleted = is_deleted;
   }

   @Override
   public String toString() {
      return "Users{" +
              "id=" + id +
              ", accountNumber=" + accountNumber +
              ", username='" + username + '\'' +
              ", password='" + password + '\'' +
              ", email='" + email + '\'' +
              ", phone='" + phone + '\'' +
              ", role='" + role + '\'' +
              ", balance=" + balance +
              ", created_at=" + created_at +
              ", status='" + status + '\'' +
              ", is_deleted=" + is_deleted +
              '}';
   }
}
