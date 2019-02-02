package com.gaoshou.common.utils;

import java.io.File;
import java.util.regex.Pattern;

import android.text.TextUtils;

public class Validator {

    public static boolean isEmpty(final String str) {
        boolean isEmpty = false;
        if (null == str || 0 == str.trim().length()) {
            isEmpty = true;
        }

        return isEmpty;
    }

    public static boolean isMobilePhoneNumber(final String str) {
        boolean isMobilePhoneNumber = true;
        if (isEmpty(str) || !Pattern.matches("^(130|131|132|133|134|135|136|137|138|139|150|151|152|153|155|156|157|158|159|176|177|178|180|185|186|187|188|189)\\d{8}$", str)) {
            isMobilePhoneNumber = false;
        }

        return isMobilePhoneNumber;
    }

    public static boolean isPhoneNumber(final String str) {
        boolean isPhoneNumber = true;
        if (isEmpty(str) || !Pattern.matches("(\\(\\d{3,4}\\)|\\d{3,4}-|\\s)?\\d{7,14}", str)) {
            isPhoneNumber = false;
        }

        return isPhoneNumber;
    }

    // 昵称长度 : 最大14位,中文7字
    public static boolean isUserName(final String str) {
        boolean isUserName = false;
        if (!isEmpty(str) && Pattern.matches("^[a-zA-Z\u4E00-\u9FA5]\\w{0,254}$", str)) {
            isUserName = true;
        }
        return isUserName;
    }

    // 真实姓名长度 : 最大14位,中文7字
    public static boolean isRealName(final String str) {
        boolean isRealName = false;
        if (isEmpty(str) || (!isEmpty(str) && Pattern.matches("^[a-zA-Z\u4E00-\u9FA5]\\w{0,254}$", str))) {
            isRealName = true;
        }
        return isRealName;
    }

    // 密码长度 : 最小长度--最小6位~最大16位;允许字符集：26个英文字母，数字0~9，键盘上常用的字符#@%等（英文半角码）
    public static boolean isPassword(final String str) {
        boolean isPassword = false;
        if (!isEmpty(str) && Pattern.matches("^[a-zA-Z0-9_#@%!?~%$&]{6,16}$", str)) {
            isPassword = true;
        }
        return isPassword;
    }

    public static boolean isChineseCharacter(final String str) {
        boolean isChineseCharacter = false;
        if (!isEmpty(str) && Pattern.matches("[^\\x00-\\xff]*", str)) {
            isChineseCharacter = true;
        }
        return isChineseCharacter;
    }

    public static boolean isEmail(final String str) {
        boolean isEmail = true;
        if (isEmpty(str) || !Pattern.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$", str)) {
            isEmail = false;
        }
        return isEmail;
    }

    public static boolean isFloatingPointNumber(final String str) {
        boolean isFloatingPointNumber = true;

        if (isEmpty(str) || !Pattern.matches("^(-?\\d+)(\\.\\d+)?$", str)) {
            isFloatingPointNumber = false;
        }
        return isFloatingPointNumber;
    }

    public static boolean isPostcode(final String str) {
        boolean isPostcode = true;

        if (isEmpty(str) || !Pattern.matches("^[1-9]{1}(\\d+){5}$", str)) {
            isPostcode = false;
        }
        return isPostcode;
    }

    public static boolean isPositiveIntegerNumber(final String str) {

        boolean isPositiveIntegerNumber = true;

        if (isEmpty(str) || !Pattern.matches("^[0-9]*[1-9][0-9]*$", str)) {
            isPositiveIntegerNumber = false;
        }
        return isPositiveIntegerNumber;
    }

    //    public static boolean isLocalPictureValid(final Picture pic) {
    //
    //        if (null == pic) {
    //            return false;
    //        }
    //        
    //        if (!isLocalFilePathValid(pic.getThumbnailLocalPath())) {
    //            return false;
    //        }
    //        
    //        if (!isLocalFilePathValid(pic.getLargeLocalPath())) {
    //            return false;
    //        }
    //        
    //        if (!isLocalFilePathValid(pic.getOrginalLocalPath())) {
    //            return false;
    //        }
    //        
    //        
    //        return true;
    //    }

    public static boolean isLocalFilePathValid(final String pathStr) {
        if (null == pathStr) {
            return false;
        }

        return isLocalFileValid(new File(pathStr));
    }

    public static boolean isLocalFileValid(final File file) {
        if (null == file) {
            return false;
        }

        return file.exists();
    }

    public static boolean isIdValid(final Object id) {
        boolean isIdValid = false;
        if (null != id) {
            if (id instanceof String) {
                String idStr = TypeUtil.getString(id);
                if (!TextUtils.isEmpty(idStr)) {
                    if (!"-1".equals(idStr) && !"null".equals(idStr)) {
                        isIdValid = true;
                    }
                }
            } else if (id instanceof Integer) {
                int inInt = TypeUtil.getInteger(id);
                if (inInt != -1) {
                    isIdValid = true;
                }
            }
        }

        return isIdValid;
    }

    public static boolean isCommentValid(String comment) {
        boolean isCommentValid = false;
        if (!TextUtils.isEmpty(comment) && comment.length() <= 140) {
            isCommentValid = true;
        }

        return isCommentValid;
    }

    public static boolean isFeedbackContentValid(String feedbackContent) {
        boolean isFeedbackContentValid = false;
        if (!TextUtils.isEmpty(feedbackContent) && feedbackContent.length() <= 250) {
            isFeedbackContentValid = true;
        }

        return isFeedbackContentValid;
    }

    public static boolean isWishTitleValid(String wishTitle) {
        boolean isWishTitleValid = false;
        if (!TextUtils.isEmpty(wishTitle) && wishTitle.length() <= 20) {
            isWishTitleValid = true;
        }

        return isWishTitleValid;
    }

    public static boolean isWishContentValid(String wishContent) {
        boolean isWishContentValid = false;
        if (!TextUtils.isEmpty(wishContent) && wishContent.length() <= 140) {
            isWishContentValid = true;
        }

        return isWishContentValid;
    }

}
