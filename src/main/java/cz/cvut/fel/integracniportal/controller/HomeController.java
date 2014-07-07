package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.dao.TestDao;
import cz.cvut.fel.integracniportal.model.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by pstrnad on 7/3/14.
 */
@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    TestDao testDao;

    @RequestMapping(method = RequestMethod.GET)
    public String home(ModelMap model) {
        Test test = new Test();
        test.setText("TEXT");
        testDao.saveTest(test);

        return "home";
    }
}
