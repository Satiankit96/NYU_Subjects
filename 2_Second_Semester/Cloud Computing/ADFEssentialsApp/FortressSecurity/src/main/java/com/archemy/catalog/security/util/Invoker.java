package com.archemy.catalog.security.util;

import org.apache.commons.cli.*;
import org.apache.directory.fortress.core.model.User;
import org.apache.directory.fortress.core.model.UserAdminRole;
import org.apache.directory.fortress.core.model.UserRole;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Properties;

/**
 * Created by Ramandeep Singh on 12/15/2016.
 */
public class Invoker {
  private static final Logger logger = LoggerFactory.getLogger(Invoker.class);

  public static void main(String[] args) {
    Invoker invoker = new Invoker();
    Options options = invoker.addOptions();
    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = null;
    try {
      cmd = parser.parse(options, args);
    } catch (ParseException e) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(" java -jar <jarname.jar> ", options);
      return;
    }
    logger.info("Beginning execution");
    if (cmd.hasOption("cuser")) {
      logger.info("Creating user");
      invoker.handleCreateUser(cmd);

    } else if (cmd.hasOption("akey")) {
      logger.info("adding key");
      invoker.handleAddKey(cmd);
    }
    else if(cmd.hasOption("cpasswd")){
      logger.info("Updating users password");
      invoker.handlePasswordChange(cmd);
    }
    logger.info("Program Ends");
    System.exit(0);
  }

  private void handlePasswordChange(CommandLine cmd) {
    String userName=null;
    if (cmd.getOptionValue("u") != null && !cmd.getOptionValue("u").isEmpty()) {
      userName = cmd.getOptionValue("u");
    } else {
      throw new RuntimeException("u [username] is required with cpasswd option");
    }
    String password = null;
    if (cmd.getOptionValue("p") != null && !cmd.getOptionValue("p").isEmpty()) {
      password = cmd.getOptionValue("p");
    } else {
      throw new RuntimeException("password is required with cpasswd option");
    }
    FortressSecurityUtil util=new FortressSecurityUtil();
    util.resetPasswordForUser(userName,password);
  }

  private void handleAddKey(CommandLine cmd) {
    String userName=null;
    if (cmd.getOptionValue("u") != null && !cmd.getOptionValue("u").isEmpty()) {
      userName = cmd.getOptionValue("u");
    } else {
      throw new RuntimeException("u [username] is required with akey option");
    }
    String keyValue=null;
    if (cmd.getOptionValue("k") != null && !cmd.getOptionValue("k").isEmpty()) {
      keyValue=cmd.getOptionValue("k");
    }
    else{
      throw new RuntimeException("k [key] key is null");
    }
    FortressSecurityUtil util=new FortressSecurityUtil();
    final List<User> users = util.searchUsers(userName);
    if(users==null||users.isEmpty()){
      throw  new RuntimeException("User does not exist");
    }
    User user=users.get(0);
    Properties props=new Properties();
    props.put("licensekey",keyValue);
    util.updateUser(user,false);

  }

  private void handleCreateUser(CommandLine cmd) {
    //create user

    String userName = null;
    if (cmd.getOptionValue("u") != null && !cmd.getOptionValue("u").isEmpty()) {
      userName = cmd.getOptionValue("u");
    } else {
      throw new RuntimeException("Username is required with cuser option");
    }
    String password = null;
    if (cmd.getOptionValue("p") != null && !cmd.getOptionValue("p").isEmpty()) {
      password = cmd.getOptionValue("p");
    } else {
      throw new RuntimeException("password is required with cuser option");
    }
    String uType = null;
    boolean adminUser = false;
    if (cmd.getOptionValue("utype") != null && !cmd.getOptionValue("utype").isEmpty()) {
      uType = cmd.getOptionValue("utype");
      if (uType.trim().equalsIgnoreCase("admin")) {
        adminUser = true;
      }
    } else {
      throw new RuntimeException("utype is required with cuser option");
    }
    String roleName = null;
    if (cmd.getOptionValue("r") != null && !cmd.getOptionValue("r").isEmpty()) {
      roleName = cmd.getOptionValue("r");
    } else {
      throw new RuntimeException("r [role name] is required with cuser option");
    }
    FortressSecurityUtil util = new FortressSecurityUtil();
    User user = new User();
    user.setUserId(userName);
    user.setPassword(password.toCharArray());
    user.setOu("users");
    //ADD licencekey property
    Properties properties = new Properties();
    properties.put("licensekey", "");
    user.addProperties(properties);
    util.createUser(user, false);
    util.assignRoleToUser(roleName,userName,adminUser);

  }



  private Options addOptions() {
    Options options = new Options();
    options.addOption("cuser", false, "creates a user");
    options.addOption("u", true, "The name of the user");
    options.addOption("p", true, "The password of the user");
    options.addOption("utype", true, "User type admin or normal");
    options.addOption("r", true, "the role name");
    options.addOption("akey", false, "to specify that you are adding a key");
    options.addOption("k", true, "the licence key value");
    options.addOption("cpasswd", false, "updates the users password");
    return options;

  }
}
