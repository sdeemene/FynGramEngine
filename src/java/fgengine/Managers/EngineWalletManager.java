/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Managers;

import fgengine.Tables.Tables;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author mac
 */
public class EngineWalletManager {

    private static DecimalFormat df = new DecimalFormat("0.00");

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetMainWalletID() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        result = DBManager.GetInt(Tables.WalletTypesTable.ID, Tables.WalletTypesTable.Table, "where " + Tables.WalletTypesTable.ID + " = " + 1);
        return result;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetPendingWalletID() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        result = DBManager.GetInt(Tables.WalletTypesTable.ID, Tables.WalletTypesTable.Table, "where " + Tables.WalletTypesTable.ID + " = " + 2);
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
    public static String CreateWallet(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        String WalletNumber = ComputeWalletNumber(UserID);
        String Balance = GetMainWalletID() + ":" + 0 + ";" + GetPendingWalletID() + ":" + 0;//1:0;2:0
        String WalletPin = ComputeWalletPin(UserID);
        HashMap<String, Object> data = new HashMap<>();
        data.put(Tables.WalletTable.UserID, UserID);
        data.put(Tables.WalletTable.Balance, Balance);
        data.put(Tables.WalletTable.WalletNumber, WalletNumber);
        data.put(Tables.WalletTable.WalletPin, WalletPin);
        result = DBManager.insertTableData(Tables.WalletTable.Table, data, "");
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
    public static String ComputeWalletNumber(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        String Firstname = EngineUserManager.GetUserFirstName(UserID);
        String Lastname = EngineUserManager.GetUserLastName(UserID);

        String FNamefirstCha = UtilityManager.GetFirstCharacterText(Firstname.toUpperCase(), 2);
        String LNfirstCha = UtilityManager.GetFirstCharacterText(Lastname.toUpperCase(), 2);

        String FNamelastCha = UtilityManager.GetLastCharacterText(Firstname.toUpperCase(), 2);
        String LNlastCha = UtilityManager.GetLastCharacterText(Lastname.toUpperCase(), 2);

        String randomString = UtilityManager.GenerateRandomNumber(2);
        result = LNlastCha + FNamefirstCha + randomString + LNfirstCha + FNamelastCha;
        return result;
    }

    /**
     *
     * @param UserID
     * @return
     */
    public static String ComputeWalletPin(int UserID) {
        String result, num = "";
        String userid = "" + UserID;
        if (userid.length() > 2) {
            num = UtilityManager.GetFirstCharacterText(userid, 2);
        } else if (userid.length() < 2) {
            num = userid + "1";
        }
        result = UtilityManager.GenerateRandomNumber(2);
        result = result + num;
        if (result.length() == 4) {
            return result;
        } else {
            if (result.length() == 3) {
                result = result + "1";
                return result;
            }
            if (result.length() == 2) {
                result = result + "01";
            }
            if (result.length() == 1) {
                result = result + UtilityManager.GenerateRandomNumber(2);
                return result;
            }

        }
        return result;
    }

    /**
     *
     * @param FromUserID
     * @param ToUserID
     * @param TransactionAmount
     * @param FromWalletTypeID
     * @param ToWalletTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String CreateWalletRecord(int FromUserID, int ToUserID, double TransactionAmount, int FromWalletTypeID, int ToWalletTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "falied";
        result = InsertWalletRecord(FromUserID, TransactionAmount, FromWalletTypeID, "Debit");
        if (result.equals("success")) {
            result = InsertWalletRecord(ToUserID, TransactionAmount, ToWalletTypeID, "Credit");
        } else {
            //refund
            InsertWalletRecord(FromUserID, TransactionAmount, FromWalletTypeID, "Credit");
        }
        return result;
    }

//    public static String Process
    /**
     *
     * @param FromUserID
     * @param ToUserID
     * @param FromWalletTypeID
     * @param ToWalletTypeID
     * @param TransactionAmount
     * @param TransactionTypeName
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String ComputeWalletRecord(int FromUserID, int ToUserID, int FromWalletTypeID, int ToWalletTypeID, double TransactionAmount, String TransactionTypeName, String Narration) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "failed";
        String Description = "";
        double ToUserOldBalance = 0.0;
        double FromUserOldBalance = 0.0;
        double ToUserNewBalance = 0.0;
        double FromUserNewBalance = 0.0;
        String toBodyMsg = "";
        String fromBodyMsg = "";
        String FromWalletName = GetWalletNameByID(FromWalletTypeID);
        String ToWalletName = GetWalletNameByID(ToWalletTypeID);
        if (TransactionTypeName.equals("Subscription Fees")) {
            result = EngineWalletManager.InsertWalletRecord(FromUserID, TransactionAmount, FromWalletTypeID, "Credit");
            Description = "Hi " + EngineUserManager.GetUserName(ToUserID) + ", \n\nYou have successfully transferred " + EngineTransactionManager.FormatNumber(TransactionAmount) + " to Fyngram Account as payment for your Subscription. \n\nCheers \nFyngram.";
            EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), Description, TransactionTypeName, ToUserID);
        } else if (TransactionTypeName.equals("Activate Supplier Account")) {
            Description = "Seller's Account Activation for " + EngineUserManager.GetUserName(FromUserID);
            toBodyMsg = "Hi " + EngineUserManager.GetUserName(FromUserID) + ", \n\nYour Seller's Account has been successfully activated, and your Subscription Fees recieved. \n\nCheers \nFyngram.";
            EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), toBodyMsg, TransactionTypeName, FromUserID);
        } else if (TransactionTypeName.equals("Fund Wallet")) {
            result = EngineWalletManager.InsertWalletRecord(FromUserID, TransactionAmount, FromWalletTypeID, "Credit");
            Description = "Hi " + EngineUserManager.GetUserName(ToUserID) + ", \n\nYou funded your FynPay Account - " + EngineWalletManager.GetUserWalletNumber(ToUserID) + " with " + EngineTransactionManager.FormatNumber(TransactionAmount);
            EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), Description, TransactionTypeName, ToUserID);
        } else if (TransactionTypeName.equals("Move Fund")) {
            if (FromWalletTypeID == 1) {
                fromBodyMsg = "Hi " + EngineUserManager.GetUserName(FromUserID) + ", \n\n" + EngineTransactionManager.FormatNumber(TransactionAmount) + " had been deducted from your " + FromWalletName + " FynPay Account. \n\nCheers \nFyngram.";
                EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), fromBodyMsg, TransactionTypeName, FromUserID);
            }
            if (ToWalletTypeID == 1) {
                toBodyMsg = "Hi " + EngineUserManager.GetUserName(ToUserID) + ", \n\n" + EngineTransactionManager.FormatNumber(TransactionAmount) + " had been credited into your " + ToWalletName + " FynPay Account. \n\nCheers \nFyngram.";
                EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), toBodyMsg, TransactionTypeName, ToUserID);
            }
            Description = EngineTransactionManager.FormatNumber(TransactionAmount) + " has been transferred from your " + GetWalletNameByID(FromWalletTypeID) + " FynPay Account to your " + GetWalletNameByID(ToWalletTypeID) + " FynPay Account. - " + Narration + ". \n\nPlease, you can also check your messages for details. \n\nCheers \nFyngram.";
        }
        FromUserOldBalance = GetUserBalance(FromUserID, FromWalletTypeID);
        ToUserOldBalance = GetUserBalance(ToUserID, ToWalletTypeID);
        result = EngineWalletManager.CreateWalletRecord(FromUserID, ToUserID, TransactionAmount, FromWalletTypeID, ToWalletTypeID);
        FromUserNewBalance = GetUserBalance(FromUserID, FromWalletTypeID);
        ToUserNewBalance = GetUserBalance(ToUserID, ToWalletTypeID);
        result = EngineTransactionManager.ComputeTransaction(FromUserID, ToUserID, FromWalletTypeID, ToWalletTypeID, TransactionAmount, TransactionTypeName, FromUserOldBalance, ToUserOldBalance, FromUserNewBalance, ToUserNewBalance, Description);
        return result;
    }

    /**
     *
     * @param UserID
     * @param TransactionAmount
     * @param WalletType
     * @param TransactionType
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String InsertWalletRecord(int UserID, double TransactionAmount, int WalletType, String TransactionType) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        df.setRoundingMode(RoundingMode.UP);
        String userbalance = DBManager.GetString(Tables.WalletTable.Balance, Tables.WalletTable.Table, "where " + Tables.WalletTable.UserID + " = " + UserID);
        if (WalletType == 1) {
            String mainBalRes = userbalance.split(";")[0];//1:0
            int mainBalID = Integer.parseInt(mainBalRes.split(":")[0]);
            double mainBalValue = Double.parseDouble(mainBalRes.split(":")[1]);
            if (TransactionType.equals("Credit")) {
                mainBalValue += TransactionAmount;
            } else if (TransactionType.equals("Debit")) {
                mainBalValue -= TransactionAmount;
            }
            String newMainBalRes = mainBalID + ":" + df.format(mainBalValue) + ";" + userbalance.split(";")[1];
            userbalance = userbalance.replace(userbalance, newMainBalRes);

        } else {
            String PendingBalRes = userbalance.split(";")[1];//2:0
            int PendingBalID = Integer.parseInt(PendingBalRes.split(":")[0]);
            double PendingBalValue = Double.parseDouble(PendingBalRes.split(":")[1]);
            if (TransactionType.equals("Credit")) {
                PendingBalValue += TransactionAmount;
            } else if (TransactionType.equals("Debit")) {
                PendingBalValue -= TransactionAmount;
            }
            String newPendingBalRes = userbalance.split(";")[0] + ";" + PendingBalID + ":" + df.format(PendingBalValue);
            userbalance = userbalance.replace(userbalance, newPendingBalRes);
        }
        result = DBManager.UpdateStringData(Tables.WalletTable.Table, Tables.WalletTable.Balance, userbalance, "where " + Tables.WalletTable.UserID + " = " + UserID);
        return result;
    }

    /**
     *
     * @param UserID
     * @param WalletType
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static Double GetUserBalance(int UserID, int WalletType) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        double result = 0.0;
        String userbalance = DBManager.GetString(Tables.WalletTable.Balance, Tables.WalletTable.Table, "where " + Tables.WalletTable.UserID + " = " + UserID);
        if (userbalance.length() > 0) {
            if (WalletType == 1) {
                String mainBalRes = userbalance.split(";")[0];//1:0
                String mainBalValue = mainBalRes.split(":")[1];
                result = Double.parseDouble(mainBalValue);
            } else if (WalletType == 2) {
                String PendingBalRes = userbalance.split(";")[1];//2:0
                String PendingBalValue = PendingBalRes.split(":")[1];
                result = Double.parseDouble(PendingBalValue);
            }
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
    public static String GetUserWalletNumber(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        result = DBManager.GetString(Tables.WalletTable.WalletNumber, Tables.WalletTable.Table, "where " + Tables.WalletTable.UserID + " = " + UserID);
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
    public static int GetUserWalletPIN(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        result = DBManager.GetInt(Tables.WalletTable.WalletPin, Tables.WalletTable.Table, "where " + Tables.WalletTable.UserID + " = " + UserID);
        return result;
    }

    /**
     *
     * @param WalletID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetWalletNameByID(int WalletID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.WalletTypesTable.Name, Tables.WalletTypesTable.Table, "where " + Tables.WalletTypesTable.ID + " = " + WalletID);
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
    public static HashMap<String, String> ComputeWalletDetails(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> data = GetWalletDetailsByUserID(UserID);
        if (!data.isEmpty()) {
            double UserBalance = GetUserBalance(UserID, GetMainWalletID());
            data.put("MainBalance", "" + UserBalance);
            double UserPendingBalance = GetUserBalance(UserID, GetPendingWalletID());
            data.put("PendingBalance", "" + UserPendingBalance);
            int usertype = EngineUserManager.GetUserTypeIDByUserID("" + UserID);
            if (usertype == 1) {

                double TotalFyngramBalance = UserBalance + UserPendingBalance;
                data.put("TotalFyngramBalance", "" + TotalFyngramBalance);

                double TotalSellerBalance = GetAllSellersMainBalance() + GetAllSellersPendingBalance();
                data.put("TotalSellerBalance", "" + TotalSellerBalance);

                double TotalCustomerBalance = GetAllCustomersBalance() + GetAllCustomersPendingBalance();
                data.put("TotalCustomerBalance", "" + TotalCustomerBalance);

                double TotalMainWallets = UserBalance + TotalSellerBalance + TotalCustomerBalance;
                data.put("TotalMainWallets", "" + TotalMainWallets);

                double TotalPendingWallets = UserPendingBalance + GetAllSellersPendingBalance() + GetAllCustomersPendingBalance();
                data.put("TotalPendingWallets", "" + TotalPendingWallets);

                double TotalShippingEarnings = EngineShippingManager.GetAllShippingBalances();
                data.put("TotalShippingEarnings", "" + TotalShippingEarnings);

            }
        }

        return data;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetWalletDetailsByUserID(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> data = DBManager.GetTableData(Tables.WalletTable.Table, "where " + Tables.WalletTable.UserID + " = " + UserID);
        return data;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static double GetAllSellersMainBalance() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        double result = 0.0;
        ArrayList<Integer> SellerIDs = EngineUserManager.GetAllSellerUsers();
        if (!SellerIDs.isEmpty()) {
            for (int sellerid : SellerIDs) {
                double sellerbal = GetUserBalance(sellerid, GetMainWalletID());
                result = result + sellerbal;
            }
        }
        return result;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static double GetAllSellersPendingBalance() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        double result = 0.0;
        ArrayList<Integer> SellerIDs = EngineUserManager.GetAllSellerUsers();
        if (!SellerIDs.isEmpty()) {
            for (int sellerid : SellerIDs) {
                double sellerbal = GetUserBalance(sellerid, GetPendingWalletID());
                result = result + sellerbal;
            }
        }
        return result;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static double GetAllCustomersBalance() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        double result = 0.0;
        ArrayList<Integer> CustomerIDs = EngineUserManager.GetAllCustomerUsers();
        if (!CustomerIDs.isEmpty()) {
            for (int sellerid : CustomerIDs) {
                double customerbal = GetUserBalance(sellerid, GetMainWalletID());
                result = result + customerbal;
            }
        }
        return result;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static double GetAllCustomersPendingBalance() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        double result = 0.0;
        ArrayList<Integer> CustomerIDs = EngineUserManager.GetAllCustomerUsers();
        if (!CustomerIDs.isEmpty()) {
            for (int sellerid : CustomerIDs) {
                double customerbal = GetUserBalance(sellerid, GetPendingWalletID());
                result = result + customerbal;
            }
        }
        return result;
    }

    /**
     *
     * @param WalletNumber
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetUseIDByWalletNumber(String WalletNumber) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.WalletTable.UserID, Tables.WalletTable.Table, "where " + Tables.WalletTable.WalletNumber + " = '" + WalletNumber + "'");
        return result;
    }
}
