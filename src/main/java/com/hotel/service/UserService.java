package com.hotel.service;

import com.hotel.dao.UserDao;
import com.hotel.model.User;
import com.hotel.util.PasswordUtil;

public class UserService {

    private final UserDao userDao = new UserDao();

    /** 注册，成功返回新用户，失败抛异常，消息给用户看 */
    public User register(String username, String password, String realName,
                         String phone, String gender) throws BookingException {
        if (username == null || username.trim().length() < 3) {
            throw new BookingException("用户名至少 3 个字符");
        }
        if (password == null || password.length() < 6) {
            throw new BookingException("密码至少 6 位");
        }
        if (phone != null && !phone.trim().isEmpty() && !phone.trim().matches("1\\d{10}")) {
            throw new BookingException("请填写正确的手机号");
        }
        username = username.trim();
        if (userDao.existsUsername(username)) {
            throw new BookingException("该用户名已被注册");
        }
        User u = new User();
        u.setUsername(username);
        u.setPassword(PasswordUtil.hash(password));
        u.setRealName(realName);
        u.setPhone(phone);
        u.setGender(gender);
        u.setRole("USER");
        int id = userDao.insert(u);
        u.setId(id);
        u.setPassword(null);
        return u;
    }

    /** 登录，成功返回用户，失败返回 null */
    public User login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        User u = userDao.findByUsername(username.trim());
        if (u == null) {
            return null;
        }
        return PasswordUtil.matches(password, u.getPassword()) ? u : null;
    }

    public void updateProfile(User u) {
        userDao.updateProfile(u);
    }

    public boolean changePassword(int userId, String oldPwd, String newPwd) throws BookingException {
        User u = userDao.findById(userId);
        if (u == null || !PasswordUtil.matches(oldPwd, u.getPassword())) {
            throw new BookingException("原密码不正确");
        }
        if (newPwd == null || newPwd.length() < 6) {
            throw new BookingException("新密码至少 6 位");
        }
        userDao.updatePassword(userId, PasswordUtil.hash(newPwd));
        return true;
    }
}
