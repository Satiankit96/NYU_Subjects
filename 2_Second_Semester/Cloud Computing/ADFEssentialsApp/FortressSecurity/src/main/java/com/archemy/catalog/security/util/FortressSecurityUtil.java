package com.archemy.catalog.security.util;

import org.apache.directory.fortress.core.*;
import org.apache.directory.fortress.core.SecurityException;
import org.apache.directory.fortress.core.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Ramandeep Singh on 12/15/2016.
 */
public class FortressSecurityUtil {

  private static final Logger logger = LoggerFactory.getLogger(FortressSecurityUtil.class);
  Session rbacSession = null;

  /**
   * This method is used to initialize a AccessMgr instance which can be used to manage access
   * policies related to normal Users ie RBAC entities
   *
   * @return {@link org.apache.directory.fortress.core.AccessMgr}
   */
  public AccessMgr createAndGetAccessMgr() {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    AccessMgr acMgr = null;
    try {
      acMgr = AccessMgrFactory.createInstance(GlobalIds.HOME);
    } catch (SecurityException e) {
      logger.error(
          "[" + methodName + "]" + "Exception occured in creating the instance of access manager",
          e);
      throw new RuntimeException("Unable to create AccessMgrInstance", e
      );
    }
    return acMgr;
  }



  /**
   * This method is used to initialize a DelAccessMgr entity which can be used to manage access
   * policies related to Admin Users i.e ARBAC entities
   *
   * @return DelAccessMgr
   */
  public DelAccessMgr createAndGetDelAccessMgr() {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    DelAccessMgr acMgr = null;
    try {
      acMgr = DelAccessMgrFactory.createInstance(GlobalIds.HOME);
    } catch (SecurityException e) {
      logger.error("[" + methodName + "]" +
          "Exception occured in creating the instance of del access manager", e);
      throw new RuntimeException("Unable to create DelAccessMgr Instance", e
      );
    }
    return acMgr;
  }

  /**
   * This method returns an instance of AdminMgr
   * This instance is then used to manage user modifications
   *
   * @return an instance of AdminMgr
   */
  private AdminMgr createAndGetAdminMgr() {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    AdminMgr mgr = null;
    try {
      mgr = AdminMgrFactory.createInstance(GlobalIds.HOME);
    } catch (SecurityException e) {
      logger.error("[" + methodName + "]" + "Unable to create AdminMgr Instance", e);
      throw new RuntimeException("Unable to create AdminMgr Instance", e
      );
    }
    return mgr;
  }

  /**
   * This method returns an instance of DelAdminMgr
   * this instance is used to manage admin users/roles
   *
   * @return an instance of DelAdminMgr
   */
  private DelAdminMgr createAndGetDelAdminMgr() {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    DelAdminMgr mgr = null;
    try {
      mgr = DelAdminMgrFactory.createInstance(GlobalIds.HOME);
    } catch (SecurityException e) {
      logger.error("[" + methodName + "]" + "Unable to create DelAdminMgr Instance", e);
      throw new RuntimeException("Unable to create DelAdminMgr Instance", e
      );
    }
    return mgr;
  }

  /**
   * This method Returns review manager instance which can be used to manage existing RBAC entities
   *
   * @return ReviewMgr
   */
  private ReviewMgr createAndGetReviewManager() {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    ReviewMgr mgr = null;
    try {
      mgr = ReviewMgrFactory.createInstance(GlobalIds.HOME);
    } catch (SecurityException e) {
      logger.error("[" + methodName + "]" + "Unable to create Review Manager Instance", e);
      throw new RuntimeException("Unable to create Review Manager Instance", e
      );
    }
    return mgr;
  }

  /**
   * This method creates an instance of DelReviewManager and returns it
   * This is used to search admin roles/permissions or create organizations
   *
   * @return An instance of DelReviewMgr
   */
  private DelReviewMgr createAndGetDelReviewManager() {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    DelReviewMgr mgr = null;
    try {
      mgr = DelReviewMgrFactory.createInstance(GlobalIds.HOME);
    } catch (SecurityException e) {
      logger.error("[" + methodName + "]" + "Unable to create DelReviewManager Instance", e);
      throw new RuntimeException("Unable to create DelReviewManager Instance", e
      );
    }
    return mgr;
  }

  @Deprecated
  /**
   * This method is used to change the users' password
   *
   * @param userId      the userId of the user
   * @param oldPassword the old password of the user
   * @param newPassword the new password of the user
   *                    @deprecated because the method does not work
   */
  public void changePassword(String userId, String oldPassword, String newPassword) {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    logger.info(FortressSecurityUtil.class.getName(), methodName);
    AdminMgr mgr = createAndGetAdminMgr();
    User user = new User();
    try {
      user.setUserId(userId);
      user.setPassword(oldPassword.toCharArray());
      mgr.changePassword(user, newPassword.toCharArray());
      logger.info(FortressSecurityUtil.class.getName(), methodName);
    } catch (SecurityException e) {
      logger.error("[" + methodName + "]" + "Unable to change the password" + e.getMessage(), e);
      throw new RuntimeException("Unable to change the password" + e.getMessage());
    }
  }

  /**
   * This method is used to reset the users' password
   *
   * @param userId      the userId of the user
   * @param newPassword the new password of the user
   */
  public void resetPasswordForUser(String userId, String newPassword) {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    logger.info(FortressSecurityUtil.class.getName(), methodName);
    AdminMgr mgr = createAndGetAdminMgr();
    User user = new User();
    try {
      user.setUserId(userId);
      mgr.resetPassword(user, newPassword.toCharArray());
      logger.info(FortressSecurityUtil.class.getName(), methodName);
    } catch (SecurityException e) {
      logger.error("[" + methodName + "]" + "Unable to change the password" + e.getMessage(), e);
      throw new RuntimeException("Unable to change the password" + e.getMessage());
    }
  }

  /**
   * This method is used to initialize a PwPolicyManager entity which can be used to manage password
   * policies for users
   *
   * @return PwPolicyMgr
   */
  private PwPolicyMgr createandGetPwPolicyManager() {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    PwPolicyMgr pwPolMgr = null;
    try {
      pwPolMgr = PwPolicyMgrFactory.createInstance(GlobalIds.HOME);
    } catch (SecurityException e) {
      logger.error("[" + methodName + "]" +
          "Exception occured in creating the instance of password policy manager", e);
      throw new RuntimeException("Unable to create password policy Instance", e
      );
    }
    return pwPolMgr;
  }

  /**
   * This method is used to search the password policies
   * in OpenLdap
   *
   * @return a list of PwPolicy objects
   */
  public List<PwPolicy> searchPasswordPolicies(String policyName) {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    PwPolicyMgr pwPolMgr = this.createandGetPwPolicyManager();
    try {
      return pwPolMgr.search(policyName);
    } catch (SecurityException e) {
      logger.error("[" + methodName + "]" + "Unable to search password policies " + e.getMessage(),
          e);
      throw new RuntimeException("Unable to search password policies", e);
    }

  }

  /**
   * Assigns the specified role to the user
   *
   * @param roleName the role to assign
   * @param userName the user to assign role to
   */
  public void assignRoleToUser(String roleName, String userName, boolean isAdmin) {
    AdminMgr adminMgr = null;
    DelAdminMgr delAdminMgr = null;
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    if (isAdmin) {
      delAdminMgr = createAndGetDelAdminMgr();
      UserAdminRole role = new UserAdminRole(userName, roleName);
      try {
        delAdminMgr.assignUser(role);
      } catch (SecurityException e) {
        logger.error(
            "[" + methodName + "]" + "Unable to assign user to the given role [" + roleName + "]",
            e);
        throw new RuntimeException("Unable to assign user to the given role", e
        );
      }
    } else {
      adminMgr = createAndGetAdminMgr();
      UserRole role = new UserRole(userName, roleName);
      try {
        adminMgr.assignUser(role);
      } catch (SecurityException e) {
        logger.error(
            "[" + methodName + "]" + "Unable to assign user to the given role [" + roleName + "]",
            e);
        throw new RuntimeException("Unable to assign user to the given role", e
        );
      }
    }
  }

  /**
   * Removes the specified role from the user
   *
   * @param roleName the role to revoke
   * @param userName the user from whom to revoke the role
   */
  public void removeRoleFromUser(String roleName, String userName, boolean isAdmin) {
    AdminMgr adminMgr = null;
    DelAdminMgr delAdminMgr = null;
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    if (isAdmin) {
      delAdminMgr = createAndGetDelAdminMgr();
      UserAdminRole role = new UserAdminRole(userName, roleName);
      try {
        delAdminMgr.deassignUser(role);
      } catch (SecurityException e) {
        logger.error("[" + methodName + "]" + "Unable to deassign user from the given admin role ["
            + roleName + "]", e);
        throw new RuntimeException("Unable to deassign user from the given role", e
        );
      }
    } else {
      adminMgr = createAndGetAdminMgr();
      UserRole role = new UserRole(userName, roleName);
      try {
        adminMgr.deassignUser(role);
      } catch (SecurityException e) {
        logger.error(
            "[" + methodName + "]" + "Unable to deassign user from the given role :" + roleName, e);
        throw new RuntimeException("Unable to assign user to the given role", e
        );
      }

    }
  }

  /**
   * It is used to generate a random password
   *
   * @return
   * @code{ generateRandomPassword();
   * }
   */
  public String generateRandomPassword() {
    String password = Long.toHexString(Double.doubleToLongBits(Math.random()));
    int passLength = password.length();
    if (passLength >= 8) {
      password = password.substring(passLength - 8, passLength);
    }

    return password;
  }

  /**
   * It is used to generate a random password
   *
   * @return
   * @code{ generateRandomPassword(8);
   * }
   */
  public String generateRandomPassword(String minLength) {
    String password = Long.toHexString(Double.doubleToLongBits(Math.random()));
    int passLength = password.length();
    if (passLength >= minLength.length()) {
      password = password.substring(passLength - minLength.length(), passLength);
    }

    return password;
  }

  /**
   * Creates a user within the default OU
   * the user is of a standard inetOrgPerson class
   * this method checks whether a user already exists in the system
   * and if it does it replaces the user
   *
   * @param user             the user with the profile attributes
   * @param generatePassword whether to generate password or not
   * @return the generatedPassword
   */
  public String createUser(User user, boolean generatePassword) {
    String generatedPassword = null;
    if (user.getPassword() != null) {
      generatedPassword = new String(user.getPassword());
    }
    boolean deleteUser = true;
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    AdminMgr adMgr = createAndGetAdminMgr();
    ReviewMgr rvMgr = createAndGetReviewManager();
    User tempUser = null;
    try {
      tempUser = rvMgr.readUser(user);
    } catch (SecurityException e) {
      if (e.getErrorId() == GlobalErrIds.USER_NOT_FOUND) {
        deleteUser = false;
      } else {
        logger.error("[" + methodName + "]" + "A system exception was encountered", e);
        throw new RuntimeException("A system exception was encountered", e
        );
      }

    }
    if (deleteUser) {
      try {
        adMgr.deleteUser(tempUser);
      } catch (SecurityException e) {
        logger.error("[" + methodName + "]" +
            "A system exception was encountered:Unable to delete the existing user", e);
        throw new RuntimeException("Unable to delete the existing user", e
        );
      }
    }

    try {
      if (generatePassword) {
        generatedPassword = generateRandomPassword();
        user.setPassword(generatedPassword.toCharArray());
      }
      adMgr.addUser(user);

    } catch (SecurityException e) {
      logger.error(
          "[" + methodName + "]" + "A system exception was encountered while creating a user", e);
      throw new RuntimeException("Unable to create the user", e);
    }
    return generatedPassword;
  }

  /**
   * Updates an existing user
   *
   * @param user             the user with the profile attributes
   * @param generatePassword whether to generate password or not
   * @return the generatedPassword
   */
  public String updateUser(User user, boolean generatePassword) {
    String generatedPassword = null;
    if (user.getPassword() != null) {
      generatedPassword = new String(user.getPassword());
    }
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    AdminMgr adMgr = createAndGetAdminMgr();
    try {
      if (generatePassword) {
        generatedPassword = generateRandomPassword();
        user.setPassword(generatedPassword.toCharArray());
      }
      adMgr.updateUser(user);
    } catch (SecurityException e) {
      logger.error(
          "[" + methodName + "]" + "A system exception was encountered while updating a user", e);
      throw new RuntimeException("Unable to update the user", e);
    }
    return generatedPassword;

  }

  /**
   * Returns a list of users based on the userId
   *
   * @param userId the user id of the user it can be partial
   * @return
   */
  public List<User> searchUsers(String userId) {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    ReviewMgr mgr = createAndGetReviewManager();
    List<User> users = null;
    User user = new User(userId);
    try {
      users = mgr.findUsers(user);
    } catch (SecurityException e) {
      logger.error("[" + methodName + "]" + "Unable to search user " + e.getMessage(), e);
      throw new RuntimeException("Unable to search user", e);
    }
    return users;
  }

  /**
   * Drops the existing user
   *
   * @param user the user that needs to be dropped
   */
  public void dropUser(User user) {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    AdminMgr adMgr = createAndGetAdminMgr();
    try {
      adMgr.deleteUser(user);
    } catch (SecurityException e) {
      logger.error("[" + methodName + "]" +
          "A system exception was encountered:Unable to delete the existing user", e);
      throw new RuntimeException("Unable to delete the existing user", e
      );
    }
  }

  /**
   * @param userName The userName to be authenticated
   * @param password The password for the user
   * @return
   */
  public Session createUserSession(String userName, String password) {
    User user = new User(userName, password.toCharArray());
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    try {
      rbacSession = createAndGetAccessMgr().createSession(user, false);
    } catch (PasswordException pwdException) {
      logger
          .error("[" + methodName + "]" + "Exception occured in validating the username/password");
      throw new RuntimeException("Exception occured in validating the username/password",
          pwdException
      );
    } catch (SecurityException e) {
      logger.error(
          "[" + methodName + "]" + "System Exception occured in creating the instance of session");
      throw new RuntimeException("[" + methodName + "]" +
          "System Exception occured in creating the instance of session", e
      );
    }
    return rbacSession;
  }

  public void setRbacSession(Session rbacSession) {
    this.rbacSession = rbacSession;
  }

  public Session getRbacSession() {
    return rbacSession;
  }
}
