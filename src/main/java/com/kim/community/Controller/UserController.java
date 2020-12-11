package com.kim.community.Controller;

import com.kim.community.Annotation.LoginRequired;
import com.kim.community.Entity.User;
import com.kim.community.Service.FollowService;
import com.kim.community.Service.LikeService;
import com.kim.community.Service.UserService;
import com.kim.community.utils.CommunityConstant;
import com.kim.community.utils.CommunityUtil;
import com.kim.community.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.juli.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FollowService followService;

    @Autowired
    private LikeService likeService;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadItem(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "沒有選擇圖片");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式錯誤");
            return "/site/setting";
        }
        fileName = CommunityUtil.generateUUID() + suffix;
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上傳文件失敗 : " + e.getMessage());
            throw new RuntimeException("上傳文件失敗, 服務器發生異常" + e);
        }
        // 更新頭像路徑
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        fileName = uploadPath + "/" + fileName;
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        response.setContentType("image/" + suffix);
        try (OutputStream out = response.getOutputStream();
             FileInputStream file = new FileInputStream(fileName)
             ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = file.read(buffer)) != -1) {
                out.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("讀取頭像文件失敗" + e.getMessage());
            e.printStackTrace();
        }
    }

    @LoginRequired
    @RequestMapping(path = "/update/password" , method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, String confirmPassword, Model model) {
        User user = hostHolder.getUser();

        if (StringUtils.isBlank(oldPassword)) {
            model.addAttribute("oldPasswordError", "舊密碼不能為空");
            return "/site/setting";
        }
        if (StringUtils.isBlank(newPassword)) {
            model.addAttribute("newPasswordError", "新密碼不能為空");
            return "/site/setting";
        }
        if (StringUtils.isBlank(confirmPassword)) {
            model.addAttribute("confirmPasswordError", "舊密碼不能為空");
            return "/site/setting";
        }
        if (!CommunityUtil.md5(oldPassword + user.getSalt()).equals(user.getPassword())) {
            model.addAttribute("oldPasswordError", "舊密碼輸入不正確");
            return "/site/setting";
        }
        if (oldPassword.equals(newPassword)) {
            model.addAttribute("newPasswordError", "新舊密碼一致");
            return "/site/setting";
        }
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("confirmPasswordError", "兩次密碼輸入不一致");
            return "/site/setting";
        }
        userService.updatePassword(user.getId(), newPassword);
        return "redirect:/logout";
    }

    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfile(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("user", user);
        model.addAttribute("userLikeCount", likeCount);
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        long followeeCount = followService.findFolloweeCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followeeCount", followeeCount);
        boolean followStatus = false;
        if (hostHolder.getUser() != null) {
            followStatus = followService.findFollowStatus(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("followStatus", followStatus);
        return "/site/profile";
    }

}
