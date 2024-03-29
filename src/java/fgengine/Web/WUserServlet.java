/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Web;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fgengine.Managers.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONObject;

/**
 *
 * @author mac
 */
public class WUserServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     * @throws java.io.UnsupportedEncodingException
     * @throws java.text.ParseException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession(true);
            String json = "";
            String json1 = "";
            String json2 = "";
            String json3 = "";
            String type = request.getParameter("type").trim();
            String empty = "none";
            String result = "";
            switch (type) {
                case "Login": {
                    String[] data = request.getParameterValues("data[]");
                    String EmailAddress = data[0].trim();
                    String Password = data[1].trim();
                    String OldSessionID = data[2].trim();
                    String App = data[3].trim();
                    JsonObject returninfo = new JsonObject();
                    int UserID = 0;
                    if (EngineUserManager.checkEmailAddressOrPhoneNumberExist(EmailAddress)) {
                        UserID = EngineUserManager.checkPasswordEmailMatch(Password, EmailAddress);
                        if (UserID != 0) {
                            String NewSessionID = "";
                            int usertypeid = EngineUserManager.GetUserTypeIDByUserID("" + UserID);
                            String usertype = "";
                            session = request.getSession(true);
                            switch (usertypeid) {
                                case 1:
                                    NewSessionID = session.getId() + "#A";
                                    usertype = "Admin";
                                    session.invalidate();
                                    break;
                                case 2:
                                    NewSessionID = session.getId() + "#S";
                                    usertype = "Seller";
                                    session.invalidate();
                                    break;
                                case 3:
                                    NewSessionID = session.getId() + "#C";
                                    usertype = "Customer";
                                    session.invalidate();
                                    break;
                                default:
                                    break;
                            }
                            if (App.equals("FynGramManager") & !usertype.equals("Customer")) {
                                EngineUserManager.UpdateUserSessionDetails(OldSessionID, NewSessionID, "" + UserID, App);
                                JsonObject dataobject = new JsonObject();
                                dataobject.addProperty("sessionid", NewSessionID);
                                dataobject.addProperty("sessiontype", usertype);
                                returninfo.add("data", dataobject);
                                returninfo.addProperty("status", "success");
                                returninfo.addProperty("msg", "Successful Login");
                            } else if (App.equals("FynGramShop") & usertype.equals("Customer")) {
                                EngineUserManager.UpdateUserSessionDetails(OldSessionID, NewSessionID, "" + UserID, App);
                                JsonObject dataobject = new JsonObject();
                                dataobject.addProperty("sessionid", NewSessionID);
                                dataobject.addProperty("sessiontype", usertype);
                                returninfo.add("data", dataobject);
                                returninfo.addProperty("status", "success");
                                returninfo.addProperty("msg", "Successful Login");
                            } else {
                                returninfo.addProperty("status", "error");
                                returninfo.addProperty("msg", "Incorrect Login Details.");
                            }
                        } else {
                            returninfo.addProperty("status", "error");
                            returninfo.addProperty("msg", "Incorrect Login Details.");
                        }
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Email or Phone Number Entered Doesn't Exist.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "GetUserDetails": {
                    String sessionid = request.getParameter("data");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    HashMap<String, Object> data = EngineUserManager.GetUserDetails(UserID);
                    JSONObject datares = new JSONObject();
                    datares.putAll(data);
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetSearchUserDetails": {
                    String UserInput = request.getParameter("data");
                    HashMap<String, String> details = EngineUserManager.GetSearchResult(UserInput, 0);
                    JSONObject datares = new JSONObject();
                    datares.putAll(details);
                    json = new Gson().toJson(datares);
                    break;
                }
                case "SaveGuest": {
                    String[] data = request.getParameterValues("data[]");
                    String IPaddress = data[0].trim();
                    String Location = data[1].trim();
                    String sessionid = session.getId() + "#G";
                    EngineUserManager.ComputeGuest(sessionid, Location, IPaddress);
                    json = new Gson().toJson(sessionid);
                    break;
                }
                case "SaveComplaints": {
                    String[] data = request.getParameterValues("data[]");
                    String sessionid = data[0].trim();
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    String Subject = data[1].trim();
                    String Description = data[2].trim();
                    result = EngineUserManager.CreateComplaint(UserID, Subject, Description);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "Your complaint has been logged and will be looked into. Thank you...");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Oh No! An error has occured. Please try again.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "NewFeatureRequest": {
                    String[] data = request.getParameterValues("data[]");
                    String Name = data[0].trim();
                    String Email = data[1].trim();
                    String Description = data[2].trim();
                    result = EngineUserManager.CreateNewFeatureRequest(Name, Email, Description);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "Your new feature suggestion has been logged and will be looked into. Thank you...");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Oh No! An error has occured. Please try again.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }

                case "SubcribeNewsletter": {
                    String[] data = request.getParameterValues("data[]");
                    String Email = data[0].trim();
                    String sessionid = data[1].trim();
                    if (sessionid.equals("")) {
                        sessionid = "" + session.getAttribute("sessionid");
                    }
                    String Option = sessionid.split("#")[1];
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int LoginID = Integer.parseInt(SessionID);
                    result = EngineUserManager.UpdateGuestEmail(LoginID, Email, Option);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "Your Email address has been added to the list. You will receive an Email from us shortly. Thank you for subscribing to our Newsletter...");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Oh No! It's our problem not yours. Please try again.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "RegisterCustomer": {
                    String[] data = request.getParameterValues("data[]");
                    String Gender = data[0].trim();
                    String Frstname = data[1].trim();
                    String Lastname = data[2].trim();
                    String EmailAddress = data[3].trim();
                    String PhoneNumber = data[4].trim();
                    String Password = data[5].trim();
                    String newsletter = data[6].trim();
                    String title = data[7].trim();
                    int NewsLetter = Integer.parseInt(newsletter);
                    int CustomerUserID = 0;
                    JsonObject returninfo = new JsonObject();
                    if (!EngineUserManager.checkEmailAddressOrPhoneNumberExist(EmailAddress)) {
                        if (!EngineUserManager.checkEmailAddressOrPhoneNumberExist(PhoneNumber)) {
                            CustomerUserID = EngineUserManager.CreateUser(EmailAddress, PhoneNumber, Password, 3, NewsLetter, Gender, "", title);
                            if (CustomerUserID != 0) {
                                result = EngineUserManager.CreateCustomer(CustomerUserID, Frstname, Lastname);
                                if (result.equals("success")) {
                                    result = EngineWalletManager.CreateWallet(CustomerUserID);
                                    if (result.equals("success")) {
                                        String msgbdy = "Congratulations!!! \n\nYou have been successfully registered as a customer on Fyngram.";
                                        EngineMessageManager.sendMessage(1, msgbdy, "Customer Account Created", CustomerUserID);
                                        String Code = "FG-" + UtilityManager.randomAlphaNumeric(7) + "#C";
                                        EngineUserManager.CreateRecovery(CustomerUserID, EmailAddress, Code);
                                        EngineEmailManager.SendingEmailOption(EmailAddress, "Customer Account Created", Code, EngineUserManager.GetUserName(CustomerUserID), "Registration", "Customer");
                                        returninfo.addProperty("status", "success");
                                        returninfo.addProperty("msg", msgbdy);

                                    } else {
                                        returninfo.addProperty("status", "error");
                                        returninfo.addProperty("msg", "Oh No! Something went wrong while creating User Account. Please try again.");
                                    }
                                } else {
                                    returninfo.addProperty("status", "error");
                                    returninfo.addProperty("msg", "Oh No! Something went wrong while creating User Account. Please try again.");

                                }
                            } else {
                                returninfo.addProperty("status", "error");
                                returninfo.addProperty("msg", "Oh No! Something went wrong while creating User Account. Please try again.");

                            }
                        } else {
                            returninfo.addProperty("status", "error");
                            returninfo.addProperty("msg", "Oh No! An account with the same Phone Number already Exists. Please use another Phone Number.");

                        }
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Oh No! An account with the same Email already Exists. Please use another Email.");

                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "GetAllCustomers": {
                    HashMap<Integer, HashMap<String, Object>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineUserManager.GetAllCustomerUsers();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, Object> details = EngineUserManager.GetUserDetails(id);
                            if (!details.isEmpty()) {
                                List.put(id, details);
                            }
                        }
                        json = new Gson().toJson(List);
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "SearchCustomers": {
                    String data = request.getParameter("data");
                    HashMap<Integer, HashMap<String, Object>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineUserManager.SearchCustomerUsers(data);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, Object> details = EngineUserManager.GetUserDetails(id);
                            if (!details.isEmpty()) {
                                List.put(id, details);
                            }
                        }
                        json = new Gson().toJson(List);
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "GetCustomerDetails": {
                    String customeruserid = request.getParameter("data");
                    int CustomerUserID = Integer.parseInt(customeruserid);
                    HashMap<String, Object> details = EngineUserManager.GetUserDetails(CustomerUserID);
                    JSONObject datares = new JSONObject();
                    datares.putAll(details);
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetAllSellers": {
                    HashMap<Integer, HashMap<String, Object>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineUserManager.GetAllSellerUsers();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, Object> details = EngineUserManager.GetUserDetails(id);
                            if (!details.isEmpty()) {
                                List.put(id, details);
                            }
                        }
                        json = new Gson().toJson(List);
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "SearchSellers": {
                    String data = request.getParameter("data");
                    HashMap<Integer, HashMap<String, Object>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineUserManager.SearchSellerUsers(data);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, Object> details = EngineUserManager.GetUserDetails(id);
                            if (!details.isEmpty()) {
                                List.put(id, details);
                            }
                        }
                        json = new Gson().toJson(List);
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "GetSellerDetails": {
                    String selleruserid = request.getParameter("data");
                    int SellerUserID = Integer.parseInt(selleruserid);
                    HashMap<String, Object> details = EngineUserManager.GetUserDetails(SellerUserID);
                    JSONObject datares = new JSONObject();
                    datares.putAll(details);
                    json = new Gson().toJson(datares);
                    break;
                }
                case "DeleteSeller": {
                    String selleruserid = request.getParameter("data");
                    int SellerUserID = Integer.parseInt(selleruserid);
                    result = EngineUserManager.DeleteSeller(SellerUserID);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The seller account has been deleted.");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Oh No! Please try again.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "DeleteCustomer": {
                    String customeruserid = request.getParameter("data");
                    int CustomerUserID = Integer.parseInt(customeruserid);
                    result = EngineUserManager.DeleteCustomer(CustomerUserID);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The customer account has been deleted.");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Oh No! Please try again.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "ActivateSellerSubscrition": {
                    String selleruserid = request.getParameter("data");
                    int SellerUserID = Integer.parseInt(selleruserid);
                    result = EngineSubscriptionManager.ActivateSellerSubscription(SellerUserID);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "Congratulations!!! \nYour account has been successfully activated. \nThank you for being part of Fyngram Onlne Store");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Oh No! Please try again.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "RegisterSeller": {
                    String[] data = request.getParameterValues("data[]");
                    String EmailAddress = data[0].trim();
                    String PhoneNumber = data[1].trim();
                    String Password = data[2].trim();
                    String Gender = data[3].trim();
                    String Frstname = data[4].trim();
                    String Lastname = data[5].trim();
                    String sellertypid = data[6].trim();
                    String subscriptiontypeid = data[7].trim();
                    String BizName = data[8].trim();
                    String BizEmail = data[9].trim();
                    String BizPhone = data[10].trim();
                    String minshippingdays = data[11].trim();
                    String maxshippingdays = data[12].trim();
                    String title = data[13].trim();
                    String cacnumber = data[14].trim();

                    int SellerUserID = 0;
                    JsonObject returninfo = new JsonObject();
                    if (!EngineUserManager.checkEmailAddressOrPhoneNumberExist(EmailAddress) || !EngineUserManager.checkEmailAddressOrPhoneNumberExist(BizName)) {
                        if (!EngineUserManager.checkEmailAddressOrPhoneNumberExist(PhoneNumber) || !EngineUserManager.checkEmailAddressOrPhoneNumberExist(BizPhone)) {
                            SellerUserID = EngineUserManager.CreateUser(EmailAddress, PhoneNumber, Password, 2, 1, Gender, "", title);
                            if (SellerUserID != 0) {
                                int SubscriptionTypeID = Integer.parseInt(subscriptiontypeid);
                                int SellerTypeID = Integer.parseInt(sellertypid);
                                result = EngineUserManager.CreateSeller(SellerUserID, Frstname, Lastname, SellerTypeID, SubscriptionTypeID);
                                if (result.equals("success")) {
                                    result = EngineWalletManager.CreateWallet(SellerUserID);
                                    if (result.equals("success")) {
                                        int MinShippingDays = Integer.parseInt(minshippingdays);
                                        int MaxShippingDays = Integer.parseInt(maxshippingdays);
                                        result = EngineUserManager.CreateSellerInformation(SellerUserID, BizName, BizEmail, BizPhone, MinShippingDays, MaxShippingDays, cacnumber);
                                        if (result.equals("success")) {
                                            String msgbdy = "Congratulations!!! \n\nYou have been successfully registered as a Seller on Fyngram, a verification code email has been sent to the Seller's registered email.";
                                            EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), msgbdy, "Seller Account Created", SellerUserID);
                                            String Code = "FG-" + UtilityManager.randomAlphaNumeric(7) + "#S";
                                            EngineUserManager.CreateRecovery(SellerUserID, EmailAddress, Code);
                                            EngineEmailManager.SendingEmailOption(EmailAddress, "Seller Account Created", Code, EngineUserManager.GetUserName(SellerUserID), "Registration", "Seller");
                                            returninfo.addProperty("status", "success");
                                            returninfo.addProperty("msg", msgbdy);
                                        } else {
                                            returninfo.addProperty("status", "error");
                                            returninfo.addProperty("msg", "Oh No! Something went wrong while creating User Account. Please try again.");
                                        }
                                    } else {
                                        returninfo.addProperty("status", "error");
                                        returninfo.addProperty("msg", "Oh No! Something went wrong while creating User Account. Please try again.");
                                    }
                                } else {
                                    returninfo.addProperty("status", "error");
                                    returninfo.addProperty("msg", "Oh No! Something went wrong while creating User Account. Please try again.");
                                }
                            } else {
                                returninfo.addProperty("status", "error");
                                returninfo.addProperty("msg", "Oh No! Something went wrong while creating User Account. Please try again.");

                            }
                        } else {
                            returninfo.addProperty("status", "error");
                            returninfo.addProperty("msg", "Oh No! An account with the same Phone Number already Exists. Please use another Phone Number.");

                        }
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Oh No! An account with the same Email already Exists. Please use another Email.");

                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "GetComplaints": {
                    ArrayList<Integer> IDs = EngineUserManager.GetComplaintIDs();
                    HashMap<Integer, HashMap<String, String>> DetailsList = new HashMap<>();
                    HashMap<String, String> details = new HashMap<>();
                    if (!IDs.isEmpty()) {
                        for (int tID : IDs) {
                            details = EngineUserManager.GetComplaintData(tID);
                            DetailsList.put(tID, details);
                        }
                        json1 = new Gson().toJson(IDs);
                        json2 = new Gson().toJson(DetailsList);
                        json3 = new Gson().toJson(IDs.size());
                        json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "GetNewFeatureSuggestions": {
                    ArrayList<Integer> IDs = EngineUserManager.GetNewFeatureSuggestionIDs();
                    HashMap<Integer, HashMap<String, String>> DetailsList = new HashMap<>();
                    HashMap<String, String> details = new HashMap<>();
                    if (!IDs.isEmpty()) {
                        for (int tID : IDs) {
                            details = EngineUserManager.GetNewfeatureSuggestionData(tID);
                            DetailsList.put(tID, details);
                        }
                        json1 = new Gson().toJson(IDs);
                        json2 = new Gson().toJson(DetailsList);
                        json3 = new Gson().toJson(IDs.size());
                        json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "ComplaintOption": {
                    String[] data = request.getParameterValues("data[]");
                    String Option = data[0].trim();
                    String complaintid = data[1].trim();
                    int ComplaintID = Integer.parseInt(complaintid);
                    String optiontext = "";
                    if (Option.equals("Delete")) {
                        result = EngineUserManager.DeleteComplaint(ComplaintID);
                        optiontext = "deleted";
                    } else {
                        result = EngineUserManager.ResolveComplaint(ComplaintID);
                        optiontext = "resolved";
                    }
                    ArrayList<Integer> IDs = EngineUserManager.GetComplaintIDs();
                    HashMap<Integer, HashMap<String, String>> DetailsList = new HashMap<>();
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The complaint has been " + optiontext + " successfully.");
                        if (!IDs.isEmpty()) {
                            for (int ID : IDs) {
                                HashMap<String, String> details = EngineUserManager.GetComplaintData(ID);
                                if (!details.isEmpty()) {
                                    DetailsList.put(ID, details);
                                }
                            }
                        }
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    json1 = new Gson().toJson(IDs);
                    json2 = new Gson().toJson(DetailsList);
                    json3 = new Gson().toJson(IDs.size());
                    String json4 = new Gson().toJson(returninfo);
                    json = "[" + json1 + "," + json2 + "," + json3 + "," + json4 + "]";
                    break;
                }
                case "NewFeatureOption": {
                    String[] data = request.getParameterValues("data[]");
                    String Option = data[0].trim();
                    String newfeatureid = data[1].trim();
                    int NewFeatureID = Integer.parseInt(newfeatureid);
                    String optiontext = "";
                    if (Option.equals("Delete")) {
                        result = EngineUserManager.DeleteNewFeature(NewFeatureID);
                        optiontext = "deleted";
                    } else {
                        result = EngineUserManager.ImplementedNewFeature(NewFeatureID);
                        optiontext = "implemented";
                    }
                    ArrayList<Integer> IDs = EngineUserManager.GetNewFeatureSuggestionIDs();
                    HashMap<Integer, HashMap<String, String>> DetailsList = new HashMap<>();
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The new feature has been " + optiontext + " successfully.");
                        if (!IDs.isEmpty()) {
                            for (int ID : IDs) {
                                HashMap<String, String> details = EngineUserManager.GetNewfeatureSuggestionData(ID);
                                if (!details.isEmpty()) {
                                    DetailsList.put(ID, details);
                                }
                            }
                        }
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    json1 = new Gson().toJson(IDs);
                    json2 = new Gson().toJson(DetailsList);
                    json3 = new Gson().toJson(IDs.size());
                    String json4 = new Gson().toJson(returninfo);
                    json = "[" + json1 + "," + json2 + "," + json3 + "," + json4 + "]";
                    break;
                }
                case "GetAllGuests": {
                    ArrayList<Integer> IDs = EngineUserManager.GetAllGuests();
                    HashMap<Integer, HashMap<String, String>> transactionDetailsList = new HashMap<>();
                    HashMap<String, String> transactiondetails = new HashMap<>();
                    if (!IDs.isEmpty()) {
                        for (int ID : IDs) {
                            transactiondetails = EngineUserManager.GetGuestData(ID);
                            transactionDetailsList.put(ID, transactiondetails);
                        }
                        json1 = new Gson().toJson(IDs);
                        json2 = new Gson().toJson(transactionDetailsList);
                        json3 = new Gson().toJson(IDs.size());
                        json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }

                    break;
                }
                case "ResetPassword": {
                    String EmailAddress = request.getParameter("data");
                    if (EngineUserManager.checkEmailAddressOrPhoneNumberExist(EmailAddress)) {
                        result = EngineUserManager.ComputeResetPassword(EmailAddress);
                    } else {
                        result = "The email provided does not exist. Please, try again.";
                    }
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "Please, check the email provided for verification details.");
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "PasswordRecovery": {
                    String[] data = request.getParameterValues("data[]");
                    String RecoveryCode = data[0].trim();
                    String NewPassword = data[1].trim();
                    result = EngineUserManager.UpdateRecoveryPassword(RecoveryCode, NewPassword);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "Your Password reset was successful. Please try logging in with the new password.");
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "UpdateProfile": {//uLastName, uFirstName, uNewsletter, uPhone, uOldPass, uNewPass
                    String[] data = request.getParameterValues("data[]");
                    String sessionid = data[0].trim();
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    String uLastName = data[1].trim();
                    String uFirstName = data[2].trim();
                    String uNewsletter = data[3].trim();
                    String uPhone = data[4].trim();
                    String uOldPass = data[5].trim();
                    String uNewPass = data[6].trim();
                    String currentPass = EngineUserManager.GetUserPasswordl(UserID);
                    JsonObject returninfo = new JsonObject();
                    int Newsletter = Integer.parseInt(uNewsletter);
                    if (currentPass.equals(uOldPass)) {
                       if (!uNewPass.equals("")) {
                            result = EngineUserManager.UpdateProfile(UserID, uLastName, uFirstName, uPhone, Newsletter, uNewPass);
                        } else {
                            result = EngineUserManager.UpdateProfile(UserID, uLastName, uFirstName, uPhone, Newsletter, currentPass);
                        }
                        if (result.equals("success")) {
                            returninfo.addProperty("status", "success");
                            returninfo.addProperty("msg", "Your Profile details has been updated successfully.");
                        } else {
                            if (!result.equals("failed")) {
                                returninfo.addProperty("msg", result);
                            } else {
                                returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                            }
                            returninfo.addProperty("status", "error");
                        }
                    } else {
                        returninfo.addProperty("msg", "Your current password is incorrect. Consider requesting for a new password!");
                        returninfo.addProperty("status", "error");
                    }

                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "ValidateAccount": {
                    String[] data = request.getParameterValues("data[]");
                    String RecoveryCode = data[0].trim();
                    String res = EngineUserManager.ConfirmAccount(RecoveryCode);
                    int UserID = Integer.parseInt(res.split("#")[1]);
                    result = res.split("#")[0];
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "Your account has been confirmed. Thank you for being part of Fyngram.");
                        JsonObject dataobject = new JsonObject();
                        int usertypeid = EngineUserManager.GetUserTypeIDByUserID("" + UserID);
                        String usertype = "";
                        String sessionid = null;
                        switch (usertypeid) {
                            case 2:
                                session.invalidate();
                                session = request.getSession(true);
                                sessionid = session.getId() + "#S";
                                usertype = "Seller";
                                EngineUserManager.CreateOrUpdateSessionID(sessionid, sessionid, "" + UserID, "" + UserID);
                                break;
                            case 3:
                                usertype = "Customer";
                                session.invalidate();
                                String OldSessionID = data[1].trim();
                                session = request.getSession(true);
                                String LoginID = EngineUserManager.GetLoginIDBySessionID(OldSessionID);
                                sessionid = session.getId() + "#C";
                                EngineUserManager.CreateOrUpdateSessionID(OldSessionID, sessionid, LoginID, "" + UserID);
                                EngineCartManager.UpdateCartUserID(LoginID, "" + UserID);
                                break;
                            default:
                                break;
                        }
                        dataobject.addProperty("sessionid", sessionid);
                        dataobject.addProperty("sessiontype", usertype);
                        returninfo.add("data", dataobject);
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws java.io.UnsupportedEncodingException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, UnsupportedEncodingException {
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException | SQLException | ParseException ex) {
            Logger.getLogger(WUserServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws java.io.UnsupportedEncodingException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, UnsupportedEncodingException {
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException | SQLException | ParseException ex) {
            Logger.getLogger(WUserServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
