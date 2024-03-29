/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Web;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fgengine.Managers.*;
import fgengine.Managers.EngineUserManager;
import java.io.IOException;
import java.io.PrintWriter;
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

/**
 *
 * @author mac
 */
public class WAddressServlet extends HttpServlet {

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws ParseException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ClassNotFoundException, SQLException, ParseException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession(true);
            String temp = "" + session.getAttribute("Id");
            String json = "";
            String json1 = "";
            String json2 = "";
            String json3 = "";
            String type = request.getParameter("type").trim();
            String empty = "none";
            String result = "";
            switch (type) {
                case "GetAddresses": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineAddressManager.GetAddressIDs(1);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineAddressManager.GetAddressData(id);
                            if (!details.isEmpty()) {
                                List.put(id, details);
                            }
                        }
                        json1 = new Gson().toJson(IDS);
                        json2 = new Gson().toJson(List);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "GetAddressTypes": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineAddressManager.GetAddressTypeIDs();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineAddressManager.GetAddressTypeData(id);
                            if (!details.isEmpty()) {
                                List.put(id, details);
                            }
                        }
                        json1 = new Gson().toJson(IDS);
                        json2 = new Gson().toJson(List);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "DeleteAddressType": {
                    String addresstypeid = request.getParameter("data");
                    int AddressTypeID = Integer.parseInt(addresstypeid);
                    result = EngineAddressManager.DeleteAddressType(AddressTypeID);
                    JsonObject returninfo = new JsonObject();
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = new ArrayList<>();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "You have successfully deleted the address type.");
                        IDS = EngineAddressManager.GetAddressTypeIDs();
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineAddressManager.GetAddressTypeData(id);
                            if (!details.isEmpty()) {
                                List.put(id, details);
                            }
                        }
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong. Please try again.");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    json1 = new Gson().toJson(IDS);
                    json2 = new Gson().toJson(List);
                    json3 = new Gson().toJson(returninfo);
                    json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    break;
                }
                case "NewAddressType": {
                    String[] data = request.getParameterValues("data[]");
                    String Name = data[0].trim();
                    String addtypeid = data[1].trim();
                    int AddressTypeID = Integer.parseInt(addtypeid);
                    String optiontext = "";
                    if (AddressTypeID == 0) {
                        optiontext = "added";
                        result = EngineAddressManager.CreateAddressType(Name);
                    } else {
                        optiontext = "edited";
                        result = EngineAddressManager.EditAddressType(AddressTypeID, Name);
                    }
                    JsonObject returninfo = new JsonObject();
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = new ArrayList<>();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "You have successfully " + optiontext + " a new address type.");
                        IDS = EngineAddressManager.GetAddressTypeIDs();
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineAddressManager.GetAddressTypeData(id);
                            if (!details.isEmpty()) {
                                List.put(id, details);
                            }
                        }
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong. Please try again.");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    json1 = new Gson().toJson(IDS);
                    json2 = new Gson().toJson(List);
                    json3 = new Gson().toJson(returninfo);
                    json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    break;
                }
                case "GetStates": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineAddressManager.GetStateIDs(157);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineAddressManager.GetStateNameData(id);
                            if (!details.isEmpty()) {
                                List.put(id, details);
                            }
                        }
                        json1 = new Gson().toJson(IDS);
                        json2 = new Gson().toJson(List);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "GetLGAs": {
                    String stateid = request.getParameter("data");
                    int StateID = Integer.parseInt(stateid);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineAddressManager.GetLGAIDs(StateID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineAddressManager.GetLGANameData(id);
                            if (!details.isEmpty()) {
                                List.put(id, details);
                            }
                        }
                        json1 = new Gson().toJson(IDS);
                        json2 = new Gson().toJson(List);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "GetTowns": {
                    String lgaid = request.getParameter("data");
                    int LgaID = Integer.parseInt(lgaid);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineAddressManager.GetTownIDs(LgaID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineAddressManager.GetTownNameData(id);
                            if (!details.isEmpty()) {
                                List.put(id, details);
                            }
                        }
                        json1 = new Gson().toJson(IDS);
                        json2 = new Gson().toJson(List);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "GetBusStops": {
                    String townid = request.getParameter("data");
                    int TownID = Integer.parseInt(townid);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineAddressManager.GetBusStopIDs(TownID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineAddressManager.GetBustopNameData(id);
                            if (!details.isEmpty()) {
                                List.put(id, details);
                            }
                        }
                        json1 = new Gson().toJson(IDS);
                        json2 = new Gson().toJson(List);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "GetStreets": {
                    String busstopid = request.getParameter("data");
                    int BusStopID = Integer.parseInt(busstopid);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineAddressManager.GetStreetIDs(BusStopID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineAddressManager.GetStreetNameData(id);
                            if (!details.isEmpty()) {
                                List.put(id, details);
                            }
                        }
                        json1 = new Gson().toJson(IDS);
                        json2 = new Gson().toJson(List);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "AddNewAddress": {
                    String[] data = request.getParameterValues("data[]");
                    String sessionid = data[0].trim();
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    String addresstype = data[1].trim();
                    int AddressTypeID = Integer.parseInt(addresstype);
                    int CountryID = 157;
                    String stateid = data[2].trim();
                    int StateID = Integer.parseInt(stateid);
                    String lgaid = data[3].trim();
                    int LgaID = Integer.parseInt(lgaid);
                    String townid = data[4].trim();
                    int TownID = Integer.parseInt(townid);
                    int BusStopID = 0;
                    String BusStop = data[5].trim();
                    if (EngineAddressManager.isNumeric(BusStop)) {
                        BusStopID = Integer.parseInt(BusStop);
                    } else {
                        BusStopID = EngineAddressManager.CreateBusStop(StateID, LgaID, TownID, BusStop);
                    }
                    String Street = data[6].trim();
                    int StreetID = 0;
                    if (EngineAddressManager.isNumeric(Street)) {
                        StreetID = Integer.parseInt(Street);
                    } else {
                        StreetID = EngineAddressManager.CreateStreet(StateID, LgaID, TownID, BusStopID, Street);
                    }

                    String CloseTo = data[7].trim();
                    String PostalCode = data[8].trim();
                    String AddressLine = data[9].trim();
                    String makedefault = data[10].trim();
                    String Phone = data[11].trim();
                    int MakeDefault = Integer.parseInt(makedefault);
                    result = EngineAddressManager.ComputeAddress(UserID, AddressTypeID, CountryID, StateID, LgaID, TownID, BusStopID, StreetID, PostalCode, CloseTo, AddressLine, MakeDefault, Phone);
                    JsonObject returninfo = new JsonObject();
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = new ArrayList<>();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "You have successfully added a new address.");
                        IDS = EngineAddressManager.GetUserAddressIDs(UserID);
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineAddressManager.GetAddressData(id);
                            if (!details.isEmpty()) {
                                List.put(id, details);
                            }
                        }
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong. Please try again.");
                        }
                        returninfo.addProperty("status", "error");
                    }

                    json1 = new Gson().toJson(IDS);
                    json2 = new Gson().toJson(List);
                    json3 = new Gson().toJson(returninfo);
                    json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    break;
                }
                case "GetUserAddresses": {
                    String sessionid = request.getParameter("data");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineAddressManager.GetUserAddressIDs(UserID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineAddressManager.GetAddressData(id);
                            if (!details.isEmpty()) {
                                List.put(id, details);
                            }
                        }
                        json1 = new Gson().toJson(IDS);
                        json2 = new Gson().toJson(List);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "DeleteUserAddresses": {
                    String[] data = request.getParameterValues("data[]");
                    String sessionid = data[0].trim();
                    String UserID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    String addressid = data[1].trim();
                    int AddressID = Integer.parseInt(addressid);
                    result = EngineAddressManager.DeleteAddressDetailByID(AddressID);
                    JsonObject returninfo = new JsonObject();
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = new ArrayList<>();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "You have successfully deleted the address.");
                        int uid = Integer.parseInt(UserID);
                        IDS = EngineAddressManager.GetUserAddressIDs(uid);
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineAddressManager.GetAddressData(id);
                            if (!details.isEmpty()) {
                                List.put(id, details);
                            }
                        }
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong. Please try again.");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    json1 = new Gson().toJson(IDS);
                    json2 = new Gson().toJson(List);
                    json3 = new Gson().toJson(returninfo);
                    json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    break;
                }
                case "MakeUserAddressDefault": {
                    String[] data = request.getParameterValues("data[]");
                    String sessionid = data[0].trim();
                    String UserID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    String addressid = data[1].trim();
                    int AddressID = Integer.parseInt(addressid);
                    result = EngineAddressManager.SetDefaultAddress(UserID, AddressID);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = new ArrayList<>();
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "Address has been successfully set to the default address..");
                        int uid = Integer.parseInt(UserID);
                        IDS = EngineAddressManager.GetUserAddressIDs(uid);
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineAddressManager.GetAddressData(id);
                            if (!details.isEmpty()) {
                                List.put(id, details);
                            }
                        }
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong. Please try again.");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    json1 = new Gson().toJson(IDS);
                    json2 = new Gson().toJson(List);
                    json3 = new Gson().toJson(returninfo);
                    json = "[" + json1 + "," + json2 + "," + json3 + "]";
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
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(WAddressServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(WAddressServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(WAddressServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(WAddressServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(WAddressServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(WAddressServlet.class.getName()).log(Level.SEVERE, null, ex);
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
