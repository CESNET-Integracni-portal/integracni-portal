package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.model.Label;
import cz.cvut.fel.integracniportal.representation.LabelRepresentation;
import cz.cvut.fel.integracniportal.service.LabelService;
import cz.cvut.fel.integracniportal.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vavat on 21. 3. 2015.
 */
@Controller
@RequestMapping("/rest")
public class LabelController extends AbstractController{

    @Autowired
    private LabelService labelService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/label", method = RequestMethod.GET)
    @ResponseBody
    public List<LabelRepresentation> getUserLabels(){
        List<Label> labelList = labelService.getAllLabels();
        List<LabelRepresentation> result = new ArrayList<LabelRepresentation>(labelList.size());
        for (Label label: labelList){
            result.add(new LabelRepresentation(label));
        }
        return result;
    }

    @PreAuthorize("hasRole('main_admin')")
    @RequestMapping(value = "/v0.2/label", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity createLabel(@RequestBody LabelRepresentation labelRepresentation, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return new ResponseEntity(resolveErrors(bindingResult), HttpStatus.BAD_REQUEST);
        }

        Label label = labelService.createLabel(labelRepresentation);
        return new ResponseEntity(new LabelRepresentation(label), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('main_admin')")
    @RequestMapping(value = "/v0.2/label/{labelid}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updateLabel(@PathVariable("labelid") Long labelId,@RequestBody LabelRepresentation labelRepresentation){
        labelService.updateLabel(labelId, labelRepresentation);
        return new ResponseEntity(labelRepresentation, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('main_admin')")
    @RequestMapping(value = "/v0.2/label/{labelid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getLabel(@PathVariable("labelid") Long labelId){
        Label label = labelService.getLabelById(labelId);
        LabelRepresentation labelRepresentation = new LabelRepresentation(label);
        return new ResponseEntity(labelRepresentation, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('main_admin')")
    @RequestMapping(value = "/v0.2/label/{labelid}", method = RequestMethod.DELETE)
    public void deleteLabel(@PathVariable("labelid") Long labelId){
        labelService.removeLabel(labelService.getLabelById(labelId));
    }
}
