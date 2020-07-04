package searchFiles;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

@WebServlet("/searchFiles")
public class SearchFiles extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public SearchFiles() {
        super();

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request,response);
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        FileModel fm = new FileModel();
        // 获取查询信息 文件名 上传时间...
        String Filename = new String(request.getParameter("XXXX"));     //要查询的文件名称
                                                                                    //System.out.println(sourceCity+"vnbvnb");
        String uploadDate = new String(request.getParameter("XXXX"));   //文件的
                                                                                    //System.out.println(destinationCity+"vnbvnb");
        //String XXXX = request.getParameter("XXXX");

        //时间格式转换
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d2 = new Date();
        ResultSet rs = null;
        try {
            d2 = sdf.parse(uploadDate);//将出发时间格式化
        } catch (Exception e) {
            e.printStackTrace();
        }
        Date d1 = new Date();
        if (d1.equals(d2) || d2.before(d1)){  //时间验证
            //生成文件查询信息
            fm.setFilename(Filename);
            fm.setUploaddate(uploadDate);

            rs = fm.searchFile();// 查询车次

            try {
                rs.last();
                int count = 0;
                count = rs.getRow();// 获取查询结果
                if (count <= 0) {// 没有文件信息
                                                                                    //logger.info("Search results not found!!");
                    String msg = "没有查询到 " + Filename + " 的文件";
                    HttpSession session = request.getSession();
                    if (session.getAttribute("type") == null) {
                        request.setAttribute("msg", msg);
                        request.getRequestDispatcher("index.jsp").forward(request, response);  //跳转到主页
                    } else {
                        request.setAttribute("msg", msg);
                        request.getRequestDispatcher("searchFile.jsp").forward(request, response);  //跳转到查询首页
                    }
                } else {// 有文件信息
                    rs.beforeFirst();
                    RequestDispatcher disp = request.getRequestDispatcher("getFileSearchResults.jsp");  //跳转到查询结果页面
                    request.setAttribute("Date", request.getParameter("date"));
                    request.setAttribute("FileInformation", rs);// 设置列车信息到request域对象中
                    disp.forward(request, response);
                }
            } catch (Exception e) {// 出错，认为未找到
                HttpSession session = request.getSession();
                String msg = "没有查询到 " + Filename + " 的文件";
                if (session.getAttribute("type") == null) {
                    request.setAttribute("msg", msg);
                    request.getRequestDispatcher("index.jsp").forward(request, response);
                } else {
                    request.setAttribute("msg", msg);
                    request.getRequestDispatcher("searchFile.jsp").forward(request, response);

                }

            }
        }else{
            HttpSession session = request.getSession();
            String msg = "日期错误";
            if (session.getAttribute("type") == null) {
                request.setAttribute("msg", msg);
                request.getRequestDispatcher("index.jsp").forward(request, response);
            } else {
                request.setAttribute("msg", msg);
                request.getRequestDispatcher("searchTrainForm.jsp").forward(request, response);

            }
        }

    }

}