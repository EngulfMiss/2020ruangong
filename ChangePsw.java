package changePassword;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;




@WebServlet("/changePsw")
public class ChangePsw extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ChangePsw() {
        super();
    }


    /**
     *
     * hs : session对象
     * msg ：响应信息
     * oldPsw ：获取输入框中输入的旧密码的文本信息
     * newPsw ：获取输入框中输入的新密码的文本信息
     * confirmPsw ：获取的输入框中输入的确认密码的文本信息
     * Validate ：密码验证的类,类中有一个静态方法validateLogin(String) 正确返回值为"";
     * 上级的session对象要setAttribute  键为：type   值为：user(用户) 或者 admin(管理员)
     * UserModel : 用户模型层类
            - setUserID()
            - setPassword()
            - setOldPassword()
            - updatePassword()
     * changePassword.jsp ：修改密码的页面
     * AdminModel : 管理员模型类
     *
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession hs = request.getSession();
        String msg;
        String oldPsw = request.getParameter("oldpassword");   //通过name=oldpassword属性的input标签获取值
        String newPsw = request.getParameter("newpassword");   //通过name=newpassword属性的input标签获取值
        String confirmPsw = request.getParameter("confirmpassword");    //通过name=confirmpassword属性的input标签获取值

        msg = Validate.validateLogin(newPsw);//  增加修改密码时的验证

        if(msg.equalsIgnoreCase("")){ //新密码满足要求
            if(newPsw.equals(confirmPsw)){ //确认密码无误
                if(String.valueOf(hs.getAttribute("type")).equalsIgnoreCase("user")){ //如果是用户
                    UserModel um = new UserModel();
                    um.setUserID(String.valueOf(session.getAttribute("userid")));
                    um.setPassword(newPsw);
                    um.setOldPassword(oldPsw);
                    if (um.updatePassword()) {// 用户修改密码
                        msg = "密码修改成功";
                    } else {
                        msg = "原密码错误";
                    }
                    request.setAttribute("msg", msg);// 要传送的数据
                    //转发
                    request.getRequestDispatcher("changePassword.jsp").forward(request, response);
                }else if(String.valueOf(hs.getAttribute("type")).equalsIgnoreCase("admin")){  //如果是管理员
                    AdminModel am = new AdminModel();
                    am.setPassword(newPsw);
                    String uid1 = request.getParameter("uname");
                    am.setUserid(uid1);
                    am.setOldPassword(oldPsw);
                    if (am.changeAdminPassword()) {// 管理员修改密码
                        msg = "密码修改成功";
                    } else {
                        msg = "原密码错误";
                    }
                    request.setAttribute("msg", msg);// 要传送的数据
                    // 跳转
                    request.getRequestDispatcher("changePassword.jsp").forward(request, response);

                }else{
                    request.setAttribute("msg","身份有误");
                    request.getRequestDispatcher("changePassword.jsp").forward(request, response);
                }
            }else{ //确认密码有误
                msg = "确认密码不一致";
                request.setAttribute("msg", msg);// 要传送的数据
                // 跳转
                request.getRequestDispatcher("changePassword.jsp").forward(request, response);
            }
        }else{ //新密码不符合要求
            request.setAttribute("msg", msg);// 要传送的数据  通过验证类已经返回msg的消息了
            // 跳转
            request.getRequestDispatcher("changePassword.jsp").forward(request, response);
        }
    }
}
