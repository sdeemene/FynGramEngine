/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Managers;

import fgengine.Tables.Tables;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author mac
 */
public class EngineUserManager {

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetAdminUserID() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        result = DBManager.GetInt(Tables.UsersTable.ID, Tables.UsersTable.Table, "where " + Tables.UsersTable.ID + " = " + 1);
        return result;
    }

    /**
     *
     * @param EmailAddress
     * @param PhoneNumber
     * @param Password
     * @param UserType
     * @param NewsLetter
     * @param Gender
     * @param DeviceToken
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int CreateUser(String EmailAddress, String PhoneNumber, String Password, int UserType, int NewsLetter, String Gender, String DeviceToken, String Title) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.UsersTable.Email, EmailAddress);
        tableData.put(Tables.UsersTable.Phone, PhoneNumber);
        tableData.put(Tables.UsersTable.Password, Password);
        tableData.put(Tables.UsersTable.Newsletters, NewsLetter);
        tableData.put(Tables.UsersTable.UserType, UserType);
        tableData.put(Tables.UsersTable.Gender, Gender);
        tableData.put(Tables.UsersTable.DeviceToken, DeviceToken);
        tableData.put(Tables.UsersTable.Title, Title);
        int userId = DBManager.insertTableDataReturnID(Tables.UsersTable.Table, tableData, "");
        DBManager.UpdateCurrentDate(Tables.UsersTable.Table, Tables.UsersTable.Date, "where " + Tables.UsersTable.ID + " = " + userId);
        DBManager.UpdateCurrentTime(Tables.UsersTable.Table, Tables.UsersTable.Time, "where " + Tables.UsersTable.ID + " = " + userId);
        return userId;
    }

    /**
     *
     * @param UserID
     * @param FirstName
     * @param LastName
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateCustomer(int UserID, String FirstName, String LastName) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.CustomersTable.UserID, UserID);
        tableData.put(Tables.CustomersTable.Firstname, FirstName);
        tableData.put(Tables.CustomersTable.Lastname, LastName);
        String result = DBManager.insertTableData(Tables.CustomersTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param UserID
     * @param FirstName
     * @param LastName
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateAdmin(int UserID, String FirstName, String LastName) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.AdminTable.UserID, UserID);
        tableData.put(Tables.AdminTable.Firstname, FirstName);
        tableData.put(Tables.AdminTable.Lastname, LastName);
        String result = DBManager.insertTableData(Tables.AdminTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param UserID
     * @param FirstName
     * @param LastName
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static void CreateAdmin1() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        try {
            int auid = EngineUserManager.CreateUser("support@fyngram.com", "0904000000", "admin", 1, 1, "Male", "", "Mr");
            System.out.println(auid);
            EngineUserManager.CreateAdmin(auid, "FynGram", "Admin");
            EngineWalletManager.CreateWallet(auid);
            String msgbdy = "Congratulations!!! \nYour account has been successfully created. Thanks for being part of FynGram Web Store.";
            EngineMessageManager.sendMessage(1, msgbdy, "Admin Account Created", auid);
            String result4 = EngineEmailManager.SendEmail("support@fyngram.com", msgbdy, "Admin Account Created");
            System.out.println(result4);
            EngineUserManager.CreateSeller(auid, "Fyngram", "Fyngram", 3, 2);
            EngineUserManager.CreateSellerInformation(auid, "Fyngram", "sales@fyngram.com", "07017085741", 3, 5, "RC1629598");
        } catch (IOException ex) {
            Logger.getLogger(EngineUserManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param UserID
     * @param FirstName
     * @param LastName
     * @param SellerTypeID
     * @param SubscriptionTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateSeller(int UserID, String FirstName, String LastName, int SellerTypeID, int SubscriptionTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.SellersTable.UserID, UserID);
        tableData.put(Tables.SellersTable.Firstname, FirstName);
        tableData.put(Tables.SellersTable.Lastname, LastName);
        tableData.put(Tables.SellersTable.SellerTypeID, SellerTypeID);
        tableData.put(Tables.SellersTable.SubscriptionTypeID, SubscriptionTypeID);
        int sellerid = DBManager.insertTableDataReturnID(Tables.SellersTable.Table, tableData, "");
        String result = DBManager.UpdateCurrentDate(Tables.SellersTable.Table, Tables.SellersTable.Date, "where " + Tables.SellersTable.ID + " = " + sellerid);
        DBManager.UpdateCurrentTime(Tables.SellersTable.Table, Tables.SellersTable.Time, "where " + Tables.SellersTable.ID + " = " + sellerid);
        return result;
    }

    /**
     *
     * @param SellerUserID
     * @param Name
     * @param Email
     * @param Phone
     * @param MinShippingDays
     * @param MaxShippingDays
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateSellerInformation(int SellerUserID, String Name, String Email, String Phone, int MinShippingDays, int MaxShippingDays, String CACNumber) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.SellerInfoTable.SellerUserID, SellerUserID);
        tableData.put(Tables.SellerInfoTable.BusinessName, Name);
        tableData.put(Tables.SellerInfoTable.BusinessEmail, Email);
        tableData.put(Tables.SellerInfoTable.BusinessPhone, Phone);
        tableData.put(Tables.SellerInfoTable.MinimumShippingDays, MinShippingDays);
        tableData.put(Tables.SellerInfoTable.MaximumShippingDays, MaxShippingDays);
        tableData.put(Tables.SellerInfoTable.CAC_Number, CACNumber);
        String result = DBManager.insertTableData(Tables.SellerInfoTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetUserFirstName(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "";
        String name = GetUserName(UserID);
        result = name.split(" ")[1];
        return result;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetUserLastName(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "";
        String name = GetUserName(UserID);
        result = name.split(" ")[0];
        return result;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetUserName(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String FirstName, LastName, groupName = GetUserTypeNameByUserID("" + UserID);
        String Name = "";
        switch (groupName) {
            case "Customer":
                LastName = DBManager.GetString(Tables.CustomersTable.Lastname, Tables.CustomersTable.Table, "where " + Tables.CustomersTable.UserID + " = " + UserID);
                FirstName = DBManager.GetString(Tables.CustomersTable.Firstname, Tables.CustomersTable.Table, "where " + Tables.CustomersTable.UserID + " = " + UserID);
                Name = FirstName + " " + LastName;
                break;
            case "Seller":
                FirstName = DBManager.GetString(Tables.SellersTable.Firstname, Tables.SellersTable.Table, "where " + Tables.SellersTable.UserID + " = " + UserID);
                LastName = DBManager.GetString(Tables.SellersTable.Lastname, Tables.SellersTable.Table, "where " + Tables.SellersTable.UserID + " = " + UserID);
                Name = FirstName + " " + LastName;
                break;
            case "Admin":
                FirstName = DBManager.GetString(Tables.AdminTable.Firstname, Tables.AdminTable.Table, "where " + Tables.AdminTable.UserID + " = " + UserID);
                LastName = DBManager.GetString(Tables.AdminTable.Lastname, Tables.AdminTable.Table, "where " + Tables.AdminTable.UserID + " = " + UserID);
                Name = FirstName + " " + LastName;
                break;
        }
        return Name;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetUserTypeNameByUserID(String UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "";
        int usertypeID = GetUserTypeIDByUserID(UserID);
        result = DBManager.GetString(Tables.UserTypesTable.Name, Tables.UserTypesTable.Table, "where " + Tables.UserTypesTable.ID + " = " + usertypeID);
        return result;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetUserTypeAbbreviationByUserID(String UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "";
        int usertypeID = GetUserTypeIDByUserID(UserID);
        result = DBManager.GetString(Tables.UserTypesTable.Abbreviation, Tables.UserTypesTable.Table, "where " + Tables.UserTypesTable.ID + " = " + usertypeID);
        return result;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetUserTypeIDByUserID(String UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.UsersTable.UserType, Tables.UsersTable.Table, "where " + Tables.UsersTable.ID + " = '" + UserID + "'");
        return result;
    }

    /**
     *
     * @param string_to_check
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static boolean CheckEmailAddressOrPhoneNumberExist(String string_to_check) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        boolean result = false;
        int usid = DBManager.GetInt(Tables.UsersTable.ID, Tables.UsersTable.Table, "where " + Tables.UsersTable.Email + " = '" + string_to_check + "' or " + Tables.UsersTable.Phone + " = '" + string_to_check + "'");
        if (usid != 0) {
            result = true;
        }
        return result;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetUserPhone(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.UsersTable.Phone, Tables.UsersTable.Table, "where " + Tables.UsersTable.ID + " = " + UserID);
        return result;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetUserEmail(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.UsersTable.Email, Tables.UsersTable.Table, "where " + Tables.UsersTable.ID + " = " + UserID);
        return result;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetUserPasswordl(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.UsersTable.Password, Tables.UsersTable.Table, "where " + Tables.UsersTable.ID + " = " + UserID);
        return result;
    }

    /**
     *
     * @param UserID
     * @param UpdateValue
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateSellerStatus(int UserID, String UpdateValue) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateStringData(Tables.SellersTable.Table, Tables.SellersTable.Status, "" + UpdateValue, "where " + Tables.SellersTable.UserID + " = " + UserID);
        return result;
    }

    /**
     *
     * @param UserID
     * @param UpdateValue
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateSellerActive(int UserID, int UpdateValue) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateIntData(Tables.SellersTable.Active, UpdateValue, Tables.SellersTable.Table, "where " + Tables.SellersTable.UserID + " = " + UserID);
        return result;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetAllCustomerUsers() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        ids = DBManager.GetIntArrayList(Tables.UsersTable.ID, Tables.UsersTable.Table, "where " + Tables.UsersTable.UserType + " = " + 3);
        return ids;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetAllSellerUsers() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        ids = DBManager.GetIntArrayList(Tables.UsersTable.ID, Tables.UsersTable.Table, "where " + Tables.UsersTable.UserType + " = " + 2);
        return ids;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetAllAdminUsers() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        ids = DBManager.GetIntArrayList(Tables.UsersTable.ID, Tables.UsersTable.Table, "where " + Tables.UsersTable.UserType + " = " + 1);
        return ids;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetAllOnlineCustomerUsers() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        ids = DBManager.GetIntArrayList(Tables.UsersTable.ID, Tables.UsersTable.Table, "where " + Tables.UsersTable.UserType + " = " + 3 + " and " + Tables.UsersTable.Online + " = " + 1);
        return ids;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetAllActiveSellerUsers() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        ids = DBManager.GetIntArrayList(Tables.SellersTable.ID, Tables.SellersTable.Table, "where " + Tables.SellersTable.Active + " = " + 1);
        return ids;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetAllUnApprovedSellerUsers() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        ids = DBManager.GetIntArrayList(Tables.SellersTable.ID, Tables.SellersTable.Table, "where " + Tables.SellersTable.Active + " != " + 1);
        return ids;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetAllOnlineSellerUsers() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        ids = DBManager.GetIntArrayList(Tables.UsersTable.ID, Tables.UsersTable.Table, "where " + Tables.UsersTable.UserType + " = " + 2 + " and " + Tables.UsersTable.Online + " = " + 1);
        return ids;
    }

    /**
     *
     * @param UserID
     * @param Subject
     * @param Description
     * @param ObjectID
     * @param ObjectType
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateComplaint(int UserID, String Subject, String Description) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.ComplaintTable.UserID, UserID);
        tableData.put(Tables.ComplaintTable.Subject, Subject);
        tableData.put(Tables.ComplaintTable.Description, Description);
        int compID = DBManager.insertTableDataReturnID(Tables.ComplaintTable.Table, tableData, "");
        DBManager.UpdateCurrentDate(Tables.ComplaintTable.Table, Tables.ComplaintTable.Date, "where " + Tables.ComplaintTable.ID + " = " + compID);
        String result = DBManager.UpdateCurrentTime(Tables.ComplaintTable.Table, Tables.ComplaintTable.Time, "where " + Tables.ComplaintTable.ID + " = " + compID);
        return result;
    }

    /**
     *
     * @param UserID
     * @param GuestID
     * @param Name
     * @param Email
     * @param Description
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateNewFeatureRequest(String Name, String Email, String Description) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.NewFeatureRequestTable.Name, Name);
        tableData.put(Tables.NewFeatureRequestTable.Email, Email);
        tableData.put(Tables.NewFeatureRequestTable.Description, Description);
        int featureID = DBManager.insertTableDataReturnID(Tables.NewFeatureRequestTable.Table, tableData, "");
        DBManager.UpdateCurrentDate(Tables.NewFeatureRequestTable.Table, Tables.NewFeatureRequestTable.Date, "where " + Tables.NewFeatureRequestTable.ID + " = " + featureID);
        String result = DBManager.UpdateCurrentTime(Tables.NewFeatureRequestTable.Table, Tables.NewFeatureRequestTable.Time, "where " + Tables.NewFeatureRequestTable.ID + " = " + featureID);
        return result;
    }

    /**
     *
     * @param IPAddress
     * @param ComputerName
     * @param Location
     * @param OperatingSystem
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int CreateGuest(String IPAddress, String ComputerName, String Location, String OperatingSystem) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.GuestTable.IPAddress, IPAddress);
        tableData.put(Tables.GuestTable.ComputerName, ComputerName);
        tableData.put(Tables.GuestTable.Location, Location);
        tableData.put(Tables.GuestTable.OperatingSystem, OperatingSystem);
        int guestid = DBManager.insertTableDataReturnID(Tables.GuestTable.Table, tableData, "");
        DBManager.UpdateCurrentDate(Tables.GuestTable.Table, Tables.GuestTable.Date, "where " + Tables.GuestTable.ID + " = " + guestid);
        DBManager.UpdateCurrentTime(Tables.GuestTable.Table, Tables.GuestTable.Time, "where " + Tables.GuestTable.ID + " = " + guestid);
        return guestid;
    }

    /**
     *
     * @param NewSessionID
     * @param Location
     * @param IPAddress
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ComputeGuest(String NewSessionID, String Location, String IPAddress) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        String ComputerName = GetGuestComputerName();
        String OperatingSystemName = GetGuestComputerOS();
        int GuestID = GetGuestIDByIPAddress(IPAddress);
        if (GuestID == 0) {
            GuestID = CreateGuest(IPAddress, ComputerName, Location, OperatingSystemName);
            result = EngineUserManager.CreateOrUpdateSessionID(NewSessionID, NewSessionID, IPAddress, IPAddress);
        } else {
            result = EngineUserManager.CreateOrUpdateSessionID(NewSessionID, NewSessionID, IPAddress, IPAddress);
        }
        return result;
    }

    /**
     *
     * @return
     */
    public static String GetGuestComputerName() {
        String result = "failed";
        try {

            InetAddress inetAddress = InetAddress.getLocalHost();
            result = inetAddress.getHostName();
            System.out.println("Host Name: " + result);

        } catch (UnknownHostException e) {
        }
        return result;
    }

    /**
     *
     * @return
     */
    public static String GetGuestSystemIPAddress() {
        String result = "failed";
        try {

            InetAddress inetAddress = InetAddress.getLocalHost();
            result = inetAddress.getHostAddress();
            System.out.println("Host Name: " + result);

        } catch (UnknownHostException e) {
        }
        return result;
    }

    /**
     *
     * @return
     */
    public static String GetGuestComputerOS() {
        String result = "failed";
        String name = "os.name";
        try {
            result = System.getProperty(name);
        } catch (Exception e) {

        }

        return result;
    }

    /**
     *
     * @param LoginID
     * @param Email
     * @param Option
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateGuestEmail(int LoginID, String Email, String Option) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        if (Option.equals("G")) {//Guest
            result = DBManager.UpdateStringData(Tables.GuestTable.Table, Tables.GuestTable.Email, Email, "where " + Tables.GuestTable.ID + " = " + LoginID);

        } else if (Option.equals("U")) {
            result = DBManager.UpdateIntData(Tables.UsersTable.Newsletters, 1, Tables.UsersTable.Table, "where " + Tables.UsersTable.ID + " = " + LoginID);
        }
        String body = "Your Email address has been added to the list. You will receive an Email from us shortly. Thank you for subscribing to our Newsletter...";
        try {
            EngineEmailManager.SendEmail(Email, body, "Fyngram Newsletter Subscriptions");
        } catch (IOException ex) {
            Logger.getLogger(EngineUserManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     *
     * @param IPAddress
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetGuestIDByIPAddress(String IPAddress) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        result = DBManager.GetInt(Tables.GuestTable.ID, Tables.GuestTable.Table, "where " + Tables.GuestTable.IPAddress + " = '" + IPAddress + "'");
        return result;
    }

    /**
     *
     * @param OldSessionID
     * @param NewSessionID
     * @param NewLoginID
     * @param OldLoginID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateOrUpdateSessionID(String OldSessionID, String NewSessionID, String OldLoginID, String NewLoginID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "";
//        int ExistingID = GetSessionIDByLoginID(OldLoginID);
//        if (ExistingID == 0) {
        ArrayList<Integer> oldids = DBManager.GetIntArrayList(Tables.SessionTable.ID, Tables.SessionTable.Table, "where " + Tables.SessionTable.LoginID + " = " + NewLoginID);
        if (!oldids.isEmpty()) {
            for (int oldid : oldids) {
                DBManager.DeleteObject(Tables.SessionTable.Table, "where " + Tables.SessionTable.ID + " = " + oldid);
            }
        }

        result = DBManager.DeleteObject(Tables.SessionTable.Table, "where " + Tables.SessionTable.SessionID + " = '" + OldSessionID + "'");

        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.SessionTable.SessionID, NewSessionID);
        tableData.put(Tables.SessionTable.LoginID, NewLoginID);
        tableData.put(Tables.SessionTable.IPAddress, OldLoginID);
        int id = DBManager.insertTableDataReturnID(Tables.SessionTable.Table, tableData, "");
        result = DBManager.UpdateCurrentTime(Tables.SessionTable.Table, Tables.SessionTable.Time, "where " + Tables.SessionTable.ID + " = " + id);
        DBManager.UpdateCurrentDate(Tables.SessionTable.Table, Tables.SessionTable.Date, "where " + Tables.SessionTable.ID + " = " + id);
//        } else {
//            result = UpdateLoginSession(NewSessionID, ExistingID, NewLoginID);
//        }

        return result;
    }

    /**
     *
     * @param LoginID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetSessionIDByLoginID(String LoginID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        result = DBManager.GetInt(Tables.SessionTable.ID, Tables.SessionTable.Table, "where " + Tables.SessionTable.LoginID + " = '" + LoginID + "' Or " + Tables.SessionTable.IPAddress + " = '" + LoginID + "'");
        return result;
    }

    /**
     *
     * @param SessionID
     * @param ExistingID
     * @param LoginID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateLoginSession(String SessionID, int ExistingID, String LoginID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "";
        int existingCount = GetSessionIDByLoginCount(ExistingID);
        int NewCount = existingCount + 1;
        DBManager.UpdateStringData(Tables.SessionTable.Table, Tables.SessionTable.SessionID, SessionID, "where " + Tables.SessionTable.ID + " = " + ExistingID);
        DBManager.UpdateStringData(Tables.SessionTable.Table, Tables.SessionTable.LoginID, LoginID, "where " + Tables.SessionTable.ID + " = " + ExistingID);
        result = DBManager.UpdateCurrentTime(Tables.SessionTable.Table, Tables.SessionTable.Time, "where " + Tables.SessionTable.ID + " = " + ExistingID);
        DBManager.UpdateCurrentDate(Tables.SessionTable.Table, Tables.SessionTable.Date, "where " + Tables.SessionTable.ID + " = " + ExistingID);
        DBManager.UpdateIntData(Tables.SessionTable.VisitCount, NewCount, Tables.SessionTable.Table, "where " + Tables.SessionTable.ID + " = " + ExistingID);
        return result;
    }

    /**
     *
     * @param ExistingID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetSessionIDByLoginCount(int ExistingID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        result = DBManager.GetInt(Tables.SessionTable.VisitCount, Tables.SessionTable.Table, "where " + Tables.SessionTable.ID + " = " + ExistingID);
        return result;
    }

    /**
     *
     * @param SessionID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetLoginIDBySessionID(String SessionID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String userid = "";
        userid = DBManager.GetString(Tables.SessionTable.LoginID, Tables.SessionTable.Table, "where " + Tables.SessionTable.SessionID + " = '" + SessionID + "'");
        if (userid.equals("none")) {
            String IPAddress = GetGuestSystemIPAddress();
            userid = GetLoginIDByIPAddress(IPAddress);
        }
        return userid;
    }

    /**
     *
     * @param SessionID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetLoginIDByIPAddress(String IPAddress) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String userid = "";
        userid = DBManager.GetString(Tables.SessionTable.LoginID, Tables.SessionTable.Table, "where " + Tables.SessionTable.IPAddress + " = '" + IPAddress + "'");
        return userid;
    }

    /**
     *
     * @param string_to_check
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static boolean checkEmailAddressOrPhoneNumberExist(String string_to_check) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        boolean result = false;
        int usid = DBManager.GetInt(Tables.UsersTable.ID, Tables.UsersTable.Table, "where " + Tables.UsersTable.Email + " = '" + string_to_check + "' or " + Tables.UsersTable.Phone + " = '" + string_to_check + "'");
        if (usid != 0) {
            result = true;
        }
        return result;
    }

    /**
     *
     * @param Password
     * @param Email_PhoneNum
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int checkPasswordEmailMatch(String Password, String Email_PhoneNum) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        String memPassword = "";
        String email = Email_PhoneNum;
        memPassword = DBManager.GetString(Tables.UsersTable.Password, Tables.UsersTable.Table, "where " + Tables.UsersTable.Email + " = '" + Email_PhoneNum + "'");
        if (memPassword.equals("none")) {
            memPassword = DBManager.GetString(Tables.UsersTable.Password, Tables.UsersTable.Table, "where " + Tables.UsersTable.Phone + " = '" + Email_PhoneNum + "'");
            email = DBManager.GetString(Tables.UsersTable.Email, Tables.UsersTable.Table, "where " + Tables.UsersTable.Phone + " = '" + Email_PhoneNum + "'");
        }
        if (memPassword.equals(Password)) {
            result = DBManager.GetInt(Tables.UsersTable.ID, Tables.UsersTable.Table, "where " + Tables.UsersTable.Email + " = '" + email + "'");
        }
        return result;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, Object> GetUserDetails(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> UserTableData = DBManager.GetTableObjectData(Tables.UsersTable.Table, "where " + Tables.UsersTable.ID + " = " + UserID);
        String usertype = GetUserTypeNameByUserID("" + UserID);
        String username = GetUserName(UserID);
        UserTableData.put("UserType", usertype);
        UserTableData.put("UserID", UserID);
        UserTableData.put("UserName", username);

        int TransactionPin = EngineWalletManager.GetUserWalletPIN(UserID);
        UserTableData.put("TransactionPin", TransactionPin);
        HashMap<String, String> userData = GetUserTypeDetails(UserID, usertype);
        if (!userData.isEmpty()) {
            JSONObject datauser = new JSONObject();
            datauser.put("userData", userData);
            UserTableData.putAll(userData);
        }
        String dt = (String) UserTableData.get(Tables.UsersTable.Date);
        String date = DateManager.readDate(dt);
        UserTableData.put(Tables.UsersTable.Date, date);
        int ImageiD = EngineImageManager.GetImageID(UserID, "Profile");
        String ImageText = EngineImageManager.GetImageTextByImageID(ImageiD);
        UserTableData.put("ImageText", ImageText);
        String tm = "" + UserTableData.get(Tables.UsersTable.Time);
        String time = DateManager.readTime(tm);
        UserTableData.put(Tables.UsersTable.Time, time);
        return UserTableData;
    }

    /**
     *
     * @param UserID
     * @param UserType
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetUserTypeDetails(int UserID, String UserType) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Data = new HashMap<>();
        if (UserType.equals("Admin")) {
            Data = GetAdminUserDetails(UserID);
        } else if (UserType.equals("Seller")) {
            Data = GetSellerUserFullDetails(UserID);
        } else if (UserType.equals("Customer")) {
            Data = GetCustomerUserFullDetails(UserID);
        }
        return Data;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetAdminUserDetails(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Data = new HashMap<>();
        Data = DBManager.GetTableData(Tables.AdminTable.Table, "where " + Tables.AdminTable.UserID + " = " + UserID);
        return Data;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetCustomerUserFullDetails(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Data = new HashMap<>();
        Data = DBManager.GetTableData(Tables.CustomersTable.Table, "where " + Tables.CustomersTable.UserID + " = " + UserID);
        if (!Data.isEmpty()) {
            //wallets
            JSONObject datawallet = new JSONObject();
            datawallet.put("WalletDetails", EngineWalletManager.ComputeWalletDetails(UserID));
            Data.putAll(datawallet);
            //orders
            ArrayList<Integer> orderCount = EngineOrderManager.GetOrderIDsByCustomerUserID(UserID);
            Data.put("ordercount", "" + orderCount.size());
            //address
            ArrayList<Integer> addressCount = EngineAddressManager.GetAddressIDs(UserID);
            Data.put("addresscount", "" + addressCount.size());
            //payments
            ArrayList<Integer> paymentCount = EnginePaymentManager.GetPaymentIDs(UserID);
            Data.put("paymentcount", "" + paymentCount.size());
            //transactions
            ArrayList<Integer> transactionCount = EngineTransactionManager.GetTransactionIDs(UserID);
            Data.put("transactioncount", "" + transactionCount.size());
            //bank details
            int bankdetid = EngineCashoutManager.GetBankDetailsIDByUserID(UserID);
            JSONObject databkdet = new JSONObject();
            databkdet.put("BankDetails", EngineCashoutManager.GetBankDetailsData(bankdetid));
            if (!databkdet.isEmpty()) {
                Data.putAll(databkdet);
            }
            //messages
            ArrayList<Integer> messageCount = EngineMessageManager.GetInboxMessageIDs(UserID);
            Data.put("msgcount", "" + messageCount.size());
            //reviews
            ArrayList<Integer> reviewCount = EngineReviewManager.GetUserRatingIDsByUserID(UserID);
            Data.put("reviewcount", "" + reviewCount.size());
            //cashout
            ArrayList<Integer> cashoutCount = EngineCashoutManager.GetCashOutIDs(UserID);
            Data.put("cashoutcount", "" + cashoutCount.size());
            //discount codes
            ArrayList<Integer> discountcodeCount = EngineDiscountManager.GetCustomerDiscountCodeIDsByCustomerUserID(UserID);
            Data.put("discountcodecount", "" + discountcodeCount.size());
        }
        return Data;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetSellerUserFullDetails(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Data = new HashMap<>();
        Data = DBManager.GetTableData(Tables.SellersTable.Table, "where " + Tables.SellersTable.UserID + " = " + UserID);
        if (!Data.isEmpty()) {
            HashMap<String, String> Data2 = EngineProductManager.GetSellerInfoData(UserID);
            if (!Data2.isEmpty()) {
                Data.putAll(Data2);
            }

            int SellerTypeID = EngineSubscriptionManager.GetSellerTypeIDBySellerUserID(UserID);
            String SellerTypeName = EngineSubscriptionManager.GetSellerTypeNameBySellerTypeID(SellerTypeID);
            Data.put("SellerTypeName", SellerTypeName);
            int SellerSubscriptionTypeID = EngineSubscriptionManager.GetSellerSubscriptionTypeIDBySellerUserID(UserID);
            String SubscriptionName = EngineSubscriptionManager.GetSellerSubscriptionTypeNameBySubscriptionTypeID(SellerSubscriptionTypeID);
            Data.put("SubscriptionName", SubscriptionName);
            double SubscriptionFeesAmount = EngineSubscriptionManager.GetSellerSubscriptionAmountBySellerTypeIDAndSubscriptionTypeID(SellerTypeID, SellerSubscriptionTypeID);
            Data.put("SubscriptionFeesAmount", "" + SubscriptionFeesAmount);
            //wallets
            JSONObject datawallet = new JSONObject();
            datawallet.put("WalletDetails", EngineWalletManager.ComputeWalletDetails(UserID));
            Data.putAll(datawallet);
            //bank details
            int bankdetid = EngineCashoutManager.GetBankDetailsIDByUserID(UserID);
            JSONObject datadet = new JSONObject();
            datadet.put("BankDetails", EngineCashoutManager.GetBankDetailsData(bankdetid));
            if (!datadet.isEmpty()) {
                Data.putAll(datadet);
            }
            //messages
            ArrayList<Integer> messageCount = EngineMessageManager.GetInboxMessageIDs(UserID);
            Data.put("msgcount", "" + messageCount.size());
            //reviews
            ArrayList<Integer> reviewCount = EngineReviewManager.GetUserRatingIDsByUserID(UserID);
            Data.put("reviewcount", "" + reviewCount.size());
            //cashout
            ArrayList<Integer> cashoutCount = EngineCashoutManager.GetCashOutIDs(UserID);
            Data.put("cashoutcount", "" + cashoutCount.size());
            //orders
            ArrayList<Integer> orderCount = EngineOrderManager.GetOrderIDsBySellerUserID(UserID);
            Data.put("ordercount", "" + orderCount.size());
//            //category

            //products 
            ArrayList<Integer> productCount = EngineProductManager.GetSellerProducts(UserID);
            Data.put("productcount", "" + productCount.size());
            //address
            JSONObject dataaddressdet = new JSONObject();
            dataaddressdet.put("AddressDetails", EngineAddressManager.GetAddressDataByUserID(UserID));
            if (!dataaddressdet.isEmpty()) {
                Data.putAll(dataaddressdet);
            }
            //payments
            ArrayList<Integer> paymentCount = EnginePaymentManager.GetPaymentIDs(UserID);
            Data.put("paymentcount", "" + paymentCount.size());
            //transactions
            ArrayList<Integer> transactionCount = EngineTransactionManager.GetTransactionIDs(UserID);
            Data.put("transactioncount", "" + transactionCount.size());

            //subscription
            int subscriptionid = EngineSubscriptionManager.GetUserSubscriptionID(UserID);
            JSONObject datasubdet = new JSONObject();
            datasubdet.put("SubscriptionDetails", EngineSubscriptionManager.GetSubscriptionData(subscriptionid));
            if (!datasubdet.isEmpty()) {
                Data.putAll(datasubdet);
            }
        }
        return Data;
    }

    /**
     *
     * @param UserInput
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetSearchResult(String UserInput, int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> details = new HashMap<>();
        String userName = "No User";
        String userPhone = "No Phone";
        String userEmail = "No Email";
        String userAcctNumber = "No Account No.";
        int resultID = 0;
        if (!UserInput.equals("")) {
            int memberID = checkVerifyingEmail(UserInput);
            if (memberID == 0) {
                int membID = checkVerifyingPhone(UserInput);
                if (membID == 0) {
                    int membrid = EngineWalletManager.GetUseIDByWalletNumber(UserInput);
                    if (membrid != 0) {
                        userName = GetUserName(membrid);
                        userPhone = GetUserPhone(membrid);
                        userEmail = GetUserEmail(membrid);
                        userAcctNumber = EngineWalletManager.GetUserWalletNumber(UserID);
                        resultID = membrid;
                    }
                } else {
                    userName = GetUserName(membID);
                    userPhone = GetUserPhone(membID);
                    userEmail = GetUserEmail(membID);
                    userAcctNumber = EngineWalletManager.GetUserWalletNumber(UserID);
                    resultID = membID;
                }
            } else {
                userName = GetUserName(memberID);
                userPhone = GetUserPhone(memberID);
                userEmail = GetUserEmail(memberID);
                userAcctNumber = EngineWalletManager.GetUserWalletNumber(UserID);
                resultID = memberID;

            }
        } else {
            userName = GetUserName(UserID);
            userPhone = GetUserPhone(UserID);
            userEmail = GetUserEmail(UserID);
            userAcctNumber = EngineWalletManager.GetUserWalletNumber(UserID);
            resultID = UserID;
        }
        details.put("Beneficiaryid", "" + resultID);
        details.put("BeneficiaryName", userName);
        details.put("BeneficiaryPhone", "" + userPhone);
        details.put("BeneficiaryEmail", "" + userEmail);
        details.put("BeneficiaryAcctNo", "" + userAcctNumber);
        return details;
    }

    /**
     *
     * @param email
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int checkVerifyingEmail(String email) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int benid = 0;
        benid = DBManager.GetInt(Tables.UsersTable.ID, Tables.UsersTable.Table, "where " + Tables.UsersTable.Email + " = '" + email + "'");
        return benid;
    }

    /**
     *
     * @param phone
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int checkVerifyingPhone(String phone) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int benid = 0;
        benid = DBManager.GetInt(Tables.UsersTable.ID, Tables.UsersTable.Table, "where " + Tables.UsersTable.Phone + " = '" + phone + "'");
        return benid;
    }

    /**
     *
     * @param SearchValue
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> SearchCustomerUsers(String SearchValue) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        //user search
        ids = DBManager.GetIntArrayList(Tables.UsersTable.ID, Tables.UsersTable.Table, "where " + Tables.UsersTable.Email + " LIKE '%" + SearchValue + "%' OR " + Tables.UsersTable.Phone + " LIKE '%" + SearchValue + "%'");
        //customer table search
        ids.addAll(DBManager.GetIntArrayList(Tables.CustomersTable.UserID, Tables.CustomersTable.Table, "where " + Tables.CustomersTable.Firstname + " LIKE '%" + SearchValue + "%' OR " + Tables.CustomersTable.Lastname + " LIKE '%" + SearchValue + "%'"));
        //remove duplicates
        ids = UtilityManager.removeDuplicatesIntegerArrayList(ids);
        return ids;
    }

    /**
     *
     * @param Name
     * @param Percentage
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateSellerType(String Name, int Percentage) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.SellerTypesTable.Name, Name);
        tableData.put(Tables.SellerTypesTable.AdminTransactionPercentage, Percentage);
        String result = DBManager.insertTableData(Tables.SellerTypesTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param SellerTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteSellerType(int SellerTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.DeleteObject(Tables.SellerTypesTable.Table, "where " + Tables.SellersTable.ID + " = " + SellerTypeID);
        return result;
    }

    /**
     *
     * @param SellerTypeID
     * @param Name
     * @param Percentage
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String EditSellerType(int SellerTypeID, String Name, int Percentage) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "";
        DBManager.UpdateIntData(Tables.SellerTypesTable.AdminTransactionPercentage, Percentage, Tables.SellerTypesTable.Table, "where " + Tables.SellersTable.ID + " = " + SellerTypeID);
        result = DBManager.UpdateStringData(Tables.SellerTypesTable.Table, Tables.SellerTypesTable.Name, Name, "where " + Tables.SellersTable.ID + " = " + SellerTypeID);
        return result;
    }

    /**
     *
     * @param SearchValue
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> SearchSellerUsers(String SearchValue) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        //user search
        ids = DBManager.GetIntArrayList(Tables.SellerInfoTable.SellerUserID, Tables.SellerInfoTable.Table, "where " + Tables.SellerInfoTable.BusinessEmail + " LIKE '%" + SearchValue + "%' OR " + Tables.SellerInfoTable.BusinessPhone + " LIKE '%" + SearchValue + "%'");
        //customer table search
        ids.addAll(DBManager.GetIntArrayList(Tables.SellersTable.UserID, Tables.SellersTable.Table, "where " + Tables.SellersTable.Firstname + " LIKE '%" + SearchValue + "%' OR " + Tables.SellersTable.Lastname + " LIKE '%" + SearchValue + "%'"));
        //remove duplicates
        ids.addAll(DBManager.GetIntArrayList(Tables.SellerInfoTable.SellerUserID, Tables.SellerInfoTable.Table, "where " + Tables.SellerInfoTable.BusinessName + " LIKE '%" + SearchValue + "%'"));
        //remove duplicates
        ids = UtilityManager.removeDuplicatesIntegerArrayList(ids);
        return ids;
    }

    public static String UpdateUserSessionDetails(String OldSessionID, String NewSessionID, String UserID, String App) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "";
        String LoginID = "";
        LoginID = EngineUserManager.GetLoginIDBySessionID(OldSessionID);
        if (LoginID.equals("none")) {
            LoginID = GetGuestSystemIPAddress();
        }
        EngineUserManager.CreateOrUpdateSessionID(OldSessionID, NewSessionID, LoginID, "" + UserID);
        if ((!App.equals("FyngramManager"))) {
            EngineCartManager.UpdateCartUserID(LoginID, "" + UserID);
        }

        return result;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetComplaintIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = DBManager.GetIntArrayListDescending(Tables.ComplaintTable.ID, Tables.ComplaintTable.Table, "ORDER BY id DESC");
        return ids;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetUserComplaintIDs(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = DBManager.GetIntArrayListDescending(Tables.ComplaintTable.ID, Tables.ComplaintTable.Table, "where " + Tables.ComplaintTable.UserID + " = " + UserID);
        return ids;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetNewFeatureSuggestionIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = DBManager.GetIntArrayListDescending(Tables.NewFeatureRequestTable.ID, Tables.NewFeatureRequestTable.Table, "ORDER BY id DESC");
        return ids;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetUserNewFeatureSuggestionIDs(String email) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = DBManager.GetIntArrayListDescending(Tables.NewFeatureRequestTable.ID, Tables.NewFeatureRequestTable.Table, "where " + Tables.NewFeatureRequestTable.Email + " = '" + email + "'");
        return ids;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetUserPasswordRecIDs(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = DBManager.GetIntArrayListDescending(Tables.PasswordRecoveryTable.ID, Tables.PasswordRecoveryTable.Table, "where " + Tables.PasswordRecoveryTable.UserID + " = " + UserID);
        return ids;
    }

    /**
     *
     * @param ComplaintID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetComplaintData(int ComplaintID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.ComplaintTable.Table, "where " + Tables.ComplaintTable.ID + " = " + ComplaintID);
        if (!Details.isEmpty()) {
            String userid = Details.get(Tables.ComplaintTable.UserID);
            int ComplaintUserID = Integer.parseInt(userid);
            Details.put("complaintUserName", GetUserName(ComplaintUserID));
            String dt = Details.get(Tables.ComplaintTable.Date);
            String date = DateManager.readDate(dt);
            Details.put(Tables.ComplaintTable.Date, date);

            String tm = Details.get(Tables.ComplaintTable.Time);
            String time = DateManager.readTime(tm);
            Details.put(Tables.ComplaintTable.Time, time);
        }
        return Details;
    }

    /**
     *
     * @param ComplaintID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetNewfeatureSuggestionData(int NewFeatureID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.NewFeatureRequestTable.Table, "where " + Tables.NewFeatureRequestTable.ID + " = " + NewFeatureID);
        if (!Details.isEmpty()) {

            String dt = Details.get(Tables.NewFeatureRequestTable.Date);
            String date = DateManager.readDate(dt);
            Details.put(Tables.NewFeatureRequestTable.Date, date);

            String tm = Details.get(Tables.NewFeatureRequestTable.Time);
            String time = DateManager.readTime(tm);
            Details.put(Tables.NewFeatureRequestTable.Time, time);
        }
        return Details;
    }

    /**
     *
     * @param ComplaintID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteComplaint(int ComplaintID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.DeleteObject(Tables.ComplaintTable.Table, "where " + Tables.ComplaintTable.ID + " = " + ComplaintID);
        return result;
    }

    /**
     *
     * @param ComplaintID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ResolveComplaint(int ComplaintID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int userid = DBManager.GetInt(Tables.ComplaintTable.UserID, Tables.ComplaintTable.Table, "where " + Tables.ComplaintTable.ID + " = " + ComplaintID);
        String result = "failed";
        String dt = DBManager.GetString(Tables.ComplaintTable.Date, Tables.ComplaintTable.Table, "where " + Tables.ComplaintTable.ID + " = " + ComplaintID);
        String date = DateManager.readDate(dt);
        String body = "Hi " + GetUserName(userid) + ",\n\nYour complaint that was logged on " + date + " has been resolved. \n\nCheers \nFyngram.";
        EngineMessageManager.sendMessage(GetAdminUserID(), body, "Complaint Revolved - Fyngram", userid);
        result = DBManager.UpdateStringData(Tables.ComplaintTable.Table, Tables.ComplaintTable.Status, "Resolved", "where " + Tables.ComplaintTable.ID + " = " + ComplaintID);
        return result;
    }

    /**
     *
     * @param NewFeatureID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ImplementedNewFeature(int NewFeatureID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, IOException {
        String email = DBManager.GetString(Tables.NewFeatureRequestTable.Email, Tables.NewFeatureRequestTable.Table, "where " + Tables.NewFeatureRequestTable.ID + " = " + NewFeatureID);
        String result = "failed";
        String dt = DBManager.GetString(Tables.NewFeatureRequestTable.Date, Tables.NewFeatureRequestTable.Table, "where " + Tables.NewFeatureRequestTable.ID + " = " + NewFeatureID);
        String date = DateManager.readDate(dt);
        String name = DBManager.GetString(Tables.NewFeatureRequestTable.Name, Tables.NewFeatureRequestTable.Table, "where " + Tables.NewFeatureRequestTable.ID + " = " + NewFeatureID);
        String body = "Hi " + name + ",\n\nYour suggestion for new feature that was logged on " + date + " has been implemented.  \n\nCheers \nFyngram.";
        EngineEmailManager.SendEmail(email, body, "New Feature Suggestion - Fyngram");
        result = DBManager.UpdateStringData(Tables.NewFeatureRequestTable.Table, Tables.NewFeatureRequestTable.Status, "Implemented", "where " + Tables.NewFeatureRequestTable.ID + " = " + NewFeatureID);
        return result;
    }

    /**
     *
     * @param NewFeatureID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteNewFeature(int NewFeatureID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.DeleteObject(Tables.NewFeatureRequestTable.Table, "where " + Tables.NewFeatureRequestTable.ID + " = " + NewFeatureID);
        return result;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetAllGuests() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        ids = DBManager.GetIntArrayList(Tables.GuestTable.ID, Tables.GuestTable.Table, "");
//        ids = DBManager.GetIntArrayList(Tables.GuestTable.ID, Tables.GuestTable.Table, "where " + Tables.GuestTable.Email + " != " + "");
        return ids;
    }

    public static HashMap<String, String> GetGuestData(int GuestID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Data = DBManager.GetTableData(Tables.GuestTable.Table, "where " + Tables.GuestTable.ID + " = " + GuestID);
        if (!Data.isEmpty()) {
            String dt = Data.get(Tables.GuestTable.Date);
            String date = DateManager.readDate(dt);
            Data.put(Tables.GuestTable.Date, date);

            String tm = Data.get(Tables.GuestTable.Time);
            String time = DateManager.readTime(tm);
            Data.put(Tables.GuestTable.Time, time);
        }
        return Data;
    }

    public static String ComputeResetPassword(String Email) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, IOException {
        String result = "failed";
        int UserID = DBManager.GetInt(Tables.UsersTable.ID, Tables.UsersTable.Table, "where " + Tables.UsersTable.Email + " = '" + Email + "'");
        String Code = "FG-" + UtilityManager.randomAlphaNumeric(7);
        result = CreateRecovery(UserID, Email, Code);
        String subject = "Password Recovery";
        EngineEmailManager.SendingEmailOption(Email, subject, Code, GetUserName(UserID), "Password", "");
        return result;
    }

    /**
     *
     * @param UserID
     * @param Email
     * @param Code
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateRecovery(int UserID, String Email, String Code) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.PasswordRecoveryTable.UserID, UserID);
        tableData.put(Tables.PasswordRecoveryTable.Email, Email);
        tableData.put(Tables.PasswordRecoveryTable.Code, Code);
        int recoveryID = DBManager.insertTableDataReturnID(Tables.PasswordRecoveryTable.Table, tableData, "");
        DBManager.UpdateCurrentDate(Tables.PasswordRecoveryTable.Table, Tables.PasswordRecoveryTable.DateUpdated, "where " + Tables.PasswordRecoveryTable.ID + " = " + recoveryID);
        String result = DBManager.UpdateCurrentTime(Tables.PasswordRecoveryTable.Table, Tables.PasswordRecoveryTable.TimeUpdated, "where " + Tables.PasswordRecoveryTable.ID + " = " + recoveryID);
        return result;
    }

    /**
     *
     * @param RecoveryCode
     * @param NewPassword
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateRecoveryPassword(String RecoveryCode, String NewPassword) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "";
        String Email = DBManager.GetString(Tables.PasswordRecoveryTable.Email, Tables.PasswordRecoveryTable.Table, "where " + Tables.PasswordRecoveryTable.Code + " = '" + RecoveryCode + "'");
        if (!Email.equals("none")) {
            int UserID = DBManager.GetInt(Tables.PasswordRecoveryTable.UserID, Tables.PasswordRecoveryTable.Table, "where " + Tables.PasswordRecoveryTable.Code + " = '" + RecoveryCode + "'");
            String uEmail = GetUserEmail(UserID);
            if (uEmail.equals(Email)) {
                result = DBManager.UpdateStringData(Tables.UsersTable.Table, Tables.UsersTable.Password, NewPassword, "where " + Tables.UsersTable.ID + " = " + UserID);
            } else {
                result = "Invalid Validation Code. Please check and try again";
            }
        } else {
            result = "Invalid Validation Code. Please check and try again";
        }
        return result;

    }

    /**
     *
     * @param RecoveryCode
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ConfirmAccount(String RecoveryCode) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        int UserID = 0;
        String Email = DBManager.GetString(Tables.PasswordRecoveryTable.Email, Tables.PasswordRecoveryTable.Table, "where " + Tables.PasswordRecoveryTable.Code + " = '" + RecoveryCode + "'");
        if (!Email.equals("none")) {
            UserID = DBManager.GetInt(Tables.PasswordRecoveryTable.UserID, Tables.PasswordRecoveryTable.Table, "where " + Tables.PasswordRecoveryTable.Code + " = '" + RecoveryCode + "'");
            String uEmail = GetUserEmail(UserID);
            if (uEmail.equals(Email)) {
                result = DBManager.UpdateIntData(Tables.UsersTable.Status, 1, Tables.UsersTable.Table, "where " + Tables.UsersTable.ID + " = " + UserID);
            } else {
                result = "Invalid Validation Code. Please check and try again";
            }
        } else {
            result = "Invalid Validation Code. Please check and try again";
        }
        return result + "#" + UserID;

    }

    /**
     *
     * @param UserID
     * @param LastName
     * @param FirstName
     * @param Phone
     * @param Newsletter
     * @param NewPassword
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateProfile(int UserID, String LastName, String FirstName, String Phone, int Newsletter, String NewPassword) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "";

        String usertype = GetUserTypeNameByUserID("" + UserID);
        if (usertype.equals("Customer")) {
            if (!FirstName.equals("") && !FirstName.equals("null")) {
                result = DBManager.UpdateStringData(Tables.CustomersTable.Table, Tables.CustomersTable.Firstname, FirstName, "where " + Tables.CustomersTable.UserID + " = " + UserID);
            }
            if (!LastName.equals("") && !LastName.equals("null")) {
                result = DBManager.UpdateStringData(Tables.CustomersTable.Table, Tables.CustomersTable.Lastname, LastName, "where " + Tables.CustomersTable.UserID + " = " + UserID);
            }
        } else if (usertype.equals("Supplier")) {
            if (!FirstName.equals("") || !FirstName.equals("null")) {
                result = DBManager.UpdateStringData(Tables.SellersTable.Table, Tables.SellersTable.Firstname, FirstName, "where " + Tables.SellersTable.UserID + " = " + UserID);
            }
            if (!LastName.equals("") && !LastName.equals("null")) {
                result = DBManager.UpdateStringData(Tables.SellersTable.Table, Tables.SellersTable.Lastname, LastName, "where " + Tables.SellersTable.UserID + " = " + UserID);
            }
        } else if (usertype.equals("Admin")) {
            if (!FirstName.equals("") && !FirstName.equals("null")) {
                result = DBManager.UpdateStringData(Tables.AdminTable.Table, Tables.AdminTable.Firstname, FirstName, "where " + Tables.AdminTable.UserID + " = " + UserID);
            }
            if (!LastName.equals("") && !LastName.equals("null")) {
                result = DBManager.UpdateStringData(Tables.AdminTable.Table, Tables.AdminTable.Lastname, LastName, "where " + Tables.AdminTable.UserID + " = " + UserID);
            }
        }

        if (!NewPassword.equals("") && !NewPassword.equals("null")) {
            result = DBManager.UpdateStringData(Tables.UsersTable.Table, Tables.UsersTable.Password, NewPassword, "where " + Tables.UsersTable.ID + " = " + UserID);
        }

        if (!Phone.equals("") && !Phone.equals("null")) {
            result = DBManager.UpdateStringData(Tables.UsersTable.Table, Tables.UsersTable.Phone, Phone, "where " + Tables.UsersTable.ID + " = " + UserID);

        }
        result = DBManager.UpdateIntData(Tables.UsersTable.Newsletters, Newsletter, Tables.UsersTable.Table, "where " + Tables.UsersTable.ID + " = " + UserID);
        return result;

    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteSeller(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "";

        //Sellers details
        result = DBManager.DeleteObject(Tables.SellersTable.Table, "where " + Tables.SellersTable.UserID + " = " + UserID);

        //info details
        DBManager.DeleteObject(Tables.SellerInfoTable.Table, "where " + Tables.SellerInfoTable.SellerUserID + " = " + UserID);

        //wallet details
        DBManager.DeleteObject(Tables.WalletTable.Table, "where " + Tables.WalletTable.UserID + " = " + UserID);

        //bank details
        DBManager.DeleteObject(Tables.BankDetailsTable.Table, "where " + Tables.BankDetailsTable.UserID + " = " + UserID);

        //messages
        ArrayList<Integer> messageIds = EngineMessageManager.GetInboxMessageIDs(UserID);
        if (!messageIds.isEmpty()) {
            for (int id : messageIds) {
                DBManager.DeleteObject(Tables.MessagesTable.Table, "where " + Tables.MessagesTable.ID + " = " + id);
            }
        }

        //reviews
        ArrayList<Integer> reviewIds = EngineReviewManager.GetUserRatingIDsByUserID(UserID);
        if (!reviewIds.isEmpty()) {
            for (int id : reviewIds) {
                DBManager.DeleteObject(Tables.ReviewsTable.Table, "where " + Tables.ReviewsTable.ID + " = " + id);
            }
        }

        //cashout
        ArrayList<Integer> cashoutIds = EngineCashoutManager.GetCashOutIDs(UserID);
        if (!cashoutIds.isEmpty()) {
            for (int id : cashoutIds) {
                DBManager.DeleteObject(Tables.CashoutTable.Table, "where " + Tables.CashoutTable.ID + " = " + id);
            }
        }

        //orders
        ArrayList<Integer> orderIds = EngineOrderManager.GetOrderIDsBySellerUserID(UserID);
        if (!orderIds.isEmpty()) {
            for (int id : orderIds) {
                String orderRef = EngineOrderManager.GetOrderReferenceNumber(id);
                DBManager.DeleteObject(Tables.OrdersTable.Table, "where " + Tables.OrdersTable.ID + " = " + id);

                ArrayList<Integer> HIds = EngineOrderManager.GetOrderHistoryIDs(id);
                if (!HIds.isEmpty()) {
                    for (int hid : HIds) {
                        DBManager.DeleteObject(Tables.OrderHistoryTable.Table, "where " + Tables.OrderHistoryTable.ID + " = " + hid);
                    }
                }

                DBManager.DeleteObject(Tables.OrderShippingMethodTable.Table, "where " + Tables.OrderShippingMethodTable.OrderID + " = " + id);
                DBManager.DeleteObject(Tables.OrderPaymentsTable.Table, "where " + Tables.OrderPaymentsTable.OrderReference + " = '" + orderRef + "'");
                DBManager.DeleteObject(Tables.OrderInvoicesTable.Table, "where " + Tables.OrderInvoicesTable.OrderID + " = " + id);

                ArrayList<Integer> HSIds = EngineOrderManager.GetOrderStatusHistoryIDs(id);
                if (!HSIds.isEmpty()) {
                    for (int hsid : HSIds) {
                        DBManager.DeleteObject(Tables.OrderStatusHistoryTable.Table, "where " + Tables.OrderStatusHistoryTable.ID + " = " + hsid);
                    }
                }

            }
        }

        //products 
        ArrayList<Integer> sProductIds = EngineProductManager.GetSellerProducts(UserID);
        if (!sProductIds.isEmpty()) {
            for (int id : sProductIds) {
                int prodid = DBManager.GetInt(Tables.SellerProductsTable.ProductID, Tables.SellerProductsTable.Table, "where " + Tables.SellerProductsTable.ID + " = " + id);

                EngineProductManager.DeleteProduct(prodid, 0, "Deleted");
                DBManager.DeleteObject(Tables.SellerProductsTable.Table, "where " + Tables.SellerProductsTable.ID + " = " + id);
            }
        }

        //address
        ArrayList<Integer> addressIds = EngineAddressManager.GetUserAddressIDs(UserID);
        if (!addressIds.isEmpty()) {
            for (int id : addressIds) {
                DBManager.DeleteObject(Tables.AddressDetailsTable.Table, "where " + Tables.AddressDetailsTable.ID + " = " + id);
            }
        }

        //payments
        ArrayList<Integer> paymentIds = EnginePaymentManager.GetPaymentIDs(UserID);
        if (!paymentIds.isEmpty()) {
            for (int id : paymentIds) {
                DBManager.DeleteObject(Tables.PaymentsTable.Table, "where " + Tables.PaymentsTable.ID + " = " + id);
            }
        }

        //transactions
        ArrayList<Integer> transactionIds = EngineTransactionManager.GetTransactionIDs(UserID);
        if (!transactionIds.isEmpty()) {
            for (int id : transactionIds) {
                DBManager.DeleteObject(Tables.TransactionsTable.Table, "where " + Tables.TransactionsTable.ID + " = " + id);
            }
        }

        //subscription
        ArrayList<Integer> subscriptionsIds = EngineSubscriptionManager.GetAllSubscriptionIDs(UserID);
        if (!subscriptionsIds.isEmpty()) {
            for (int id : subscriptionsIds) {
                DBManager.DeleteObject(Tables.SellerSubscriptionTable.Table, "where " + Tables.SellerSubscriptionTable.ID + " = " + id);
            }
        }

        //Delete password recovery data
        ArrayList<Integer> passIds = EngineUserManager.GetUserPasswordRecIDs(UserID);
        if (!passIds.isEmpty()) {
            for (int id : passIds) {
                DBManager.DeleteObject(Tables.PasswordRecoveryTable.Table, "where " + Tables.PasswordRecoveryTable.ID + " = " + id);
            }
        }

        //Update stock movement
        ArrayList<Integer> stockIds = EngineStockManager.GetStockIDs(UserID);
        if (!stockIds.isEmpty()) {
            for (int id : stockIds) {
                DBManager.DeleteObject(Tables.StockMovementTable.Table, "where " + Tables.StockMovementTable.ID + " = " + id);
            }
        }
        //Delete from the user table
        result = DBManager.DeleteObject(Tables.UsersTable.Table, "where " + Tables.UsersTable.ID + " = " + UserID);

        return result;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteCustomer(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "";
        //address
        ArrayList<Integer> GetUserAddressIds = EngineAddressManager.GetUserAddressIDs(UserID);
        if (!GetUserAddressIds.isEmpty()) {
            for (int id : GetUserAddressIds) {
                DBManager.DeleteObject(Tables.AddressDetailsTable.Table, "where " + Tables.AddressDetailsTable.ID + " = " + id);
            }
        }

        //reviews
        ArrayList<Integer> reviewIds = EngineReviewManager.GetUserRatingIDsByUserID(UserID);
        if (!reviewIds.isEmpty()) {
            for (int id : reviewIds) {
                DBManager.DeleteObject(Tables.ReviewsTable.Table, "where " + Tables.ReviewsTable.ID + " = " + id);
            }
        }

        //payments
        ArrayList<Integer> paymentIds = EnginePaymentManager.GetPaymentIDs(UserID);
        if (!paymentIds.isEmpty()) {
            for (int id : paymentIds) {
                DBManager.DeleteObject(Tables.PaymentsTable.Table, "where " + Tables.PaymentsTable.ID + " = " + id);
            }
        }

        //orders
        ArrayList<Integer> orderIds = EngineOrderManager.GetOrderIDsByCustomerUserID(UserID);
        if (!orderIds.isEmpty()) {
            for (int id : orderIds) {
                String orderRef = EngineOrderManager.GetOrderReferenceNumber(id);
                DBManager.DeleteObject(Tables.OrdersTable.Table, "where " + Tables.OrdersTable.ID + " = " + id);

                ArrayList<Integer> HIds = EngineOrderManager.GetOrderHistoryIDs(id);
                if (!HIds.isEmpty()) {
                    for (int hid : HIds) {
                        DBManager.DeleteObject(Tables.OrderHistoryTable.Table, "where " + Tables.OrderHistoryTable.ID + " = " + hid);
                    }
                }

                DBManager.DeleteObject(Tables.OrderShippingMethodTable.Table, "where " + Tables.OrderShippingMethodTable.OrderID + " = " + id);
                DBManager.DeleteObject(Tables.OrderPaymentsTable.Table, "where " + Tables.OrderPaymentsTable.OrderReference + " = '" + orderRef + "'");
                DBManager.DeleteObject(Tables.OrderInvoicesTable.Table, "where " + Tables.OrderInvoicesTable.OrderID + " = " + id);

                ArrayList<Integer> HSIds = EngineOrderManager.GetOrderStatusHistoryIDs(id);
                if (!HSIds.isEmpty()) {
                    for (int hsid : HSIds) {
                        DBManager.DeleteObject(Tables.OrderStatusHistoryTable.Table, "where " + Tables.OrderStatusHistoryTable.ID + " = " + hsid);
                    }
                }

            }
        }

        //Delete customer suggestions
        String Email = GetUserEmail(UserID);
        ArrayList<Integer> nfIDs = EngineUserManager.GetUserNewFeatureSuggestionIDs(Email);
        if (!nfIDs.isEmpty()) {
            for (int id : nfIDs) {
                DBManager.DeleteObject(Tables.NewFeatureRequestTable.Table, "where " + Tables.NewFeatureRequestTable.ID + " = " + id);
            }
        }

        //Delete password recovery data
        ArrayList<Integer> passIds = EngineUserManager.GetUserPasswordRecIDs(UserID);
        if (!passIds.isEmpty()) {
            for (int id : passIds) {
                DBManager.DeleteObject(Tables.PasswordRecoveryTable.Table, "where " + Tables.PasswordRecoveryTable.ID + " = " + id);
            }
        }

        //Update stock movement
        ArrayList<Integer> stockIds = EngineStockManager.GetStockIDs(UserID);
        if (!stockIds.isEmpty()) {
            for (int id : stockIds) {
                DBManager.DeleteObject(Tables.StockMovementTable.Table, "where " + Tables.StockMovementTable.ID + " = " + id);
            }
        }

        //discount
        ArrayList<Integer> discountsIds = EngineDiscountManager.GetCustomerDiscountCodeIDsByCustomerUserID(UserID);
        if (!discountsIds.isEmpty()) {
            for (int id : discountsIds) {
                DBManager.DeleteObject(Tables.CustomerDiscountCodesTable.Table, "where " + Tables.CustomerDiscountCodesTable.ID + " = " + id);
            }
        }

        //wallet details
        result = DBManager.DeleteObject(Tables.WalletTable.Table, "where " + Tables.WalletTable.UserID + " = " + UserID);

        //messages
        ArrayList<Integer> messageIds = EngineMessageManager.GetInboxMessageIDs(UserID);
        if (!messageIds.isEmpty()) {
            for (int id : messageIds) {
                DBManager.DeleteObject(Tables.MessagesTable.Table, "where " + Tables.MessagesTable.ID + " = " + id);
            }
        }

        //transactions
        ArrayList<Integer> transactionIds = EngineTransactionManager.GetTransactionIDs(UserID);
        if (!transactionIds.isEmpty()) {
            for (int id : transactionIds) {
                DBManager.DeleteObject(Tables.TransactionsTable.Table, "where " + Tables.TransactionsTable.ID + " = " + id);
            }
        }

        //bank details
        result = DBManager.DeleteObject(Tables.BankDetailsTable.Table, "where " + Tables.BankDetailsTable.UserID + " = " + UserID);

        //cashout
        ArrayList<Integer> cashoutIds = EngineCashoutManager.GetCashOutIDs(UserID);
        if (!cashoutIds.isEmpty()) {
            for (int id : cashoutIds) {
                DBManager.DeleteObject(Tables.CashoutTable.Table, "where " + Tables.CashoutTable.ID + " = " + id);
            }
        }

        //cart
        result = EngineCartManager.DeleteCartByUserID("" + UserID);

        //wishlist
        result = EngineCartManager.DeleteWishListByUserID("" + UserID);

        //complaint
        ArrayList<Integer> compIds = EngineUserManager.GetUserComplaintIDs(UserID);
        if (!compIds.isEmpty()) {
            for (int id : compIds) {
                DBManager.DeleteObject(Tables.ComplaintTable.Table, "where " + Tables.ComplaintTable.ID + " = " + id);
            }
        }

        //customer
        result = DBManager.DeleteObject(Tables.CustomersTable.Table, "where " + Tables.CustomersTable.UserID + " = " + UserID);

        //Delete the user
        result = DBManager.DeleteObject(Tables.UsersTable.Table, "where " + Tables.UsersTable.ID + " = " + UserID);

        return result;
    }

}
