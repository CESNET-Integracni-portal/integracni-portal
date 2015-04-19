package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.model.Label;
import cz.cvut.fel.integracniportal.representation.LabelMigrationRepresentation;
import cz.cvut.fel.integracniportal.representation.LabelRepresentation;
import cz.cvut.fel.integracniportal.service.LabelService;
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
    public List<LabelRepresentation> getLabels(){
        List<Label> labelList = labelService.getAllLabels();
        List<LabelRepresentation> result = new ArrayList<LabelRepresentation>(labelList.size());
        for (Label label: labelList){
            result.add(new LabelRepresentation(label));
        }
        return result;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/label", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity createLabel(@RequestBody LabelRepresentation labelRepresentation, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return new ResponseEntity(resolveErrors(bindingResult), HttpStatus.BAD_REQUEST);
        }

        Label label = labelService.createLabel(labelRepresentation);
        return new ResponseEntity(new LabelRepresentation(label), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/label/{labelid}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity updateLabel(@PathVariable("labelid") Long labelId,@RequestBody LabelRepresentation labelRepresentation){
        labelService.updateLabel(labelId, labelRepresentation);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/label/{labelid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getLabel(@PathVariable("labelid") Long labelId){
        Label label = labelService.getLabelById(labelId);
        LabelRepresentation labelRepresentation = new LabelRepresentation(label);
        return new ResponseEntity(labelRepresentation, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/label/{labelid}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity deleteLabel(@PathVariable("labelid") Long labelId){
        labelService.removeLabel(labelService.getLabelById(labelId));
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/folder/{folderId}/addLabel", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity addLabelToFolder(@PathVariable("folderId") Long id, @RequestBody LabelMigrationRepresentation representation){
        labelService.addLabelToFolder(id,representation);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/folder/{folderId}/removeLabel", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity removeLabelFromFolder(@PathVariable("folderId") Long id, @RequestBody LabelMigrationRepresentation representation){
        labelService.removeLabelFromFolder(id, representation);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/file/{fileId}/addLabel", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity addLabelToFile(@PathVariable("fileId") String id, @RequestBody LabelMigrationRepresentation representation){
        labelService.addLabelToFile(id,representation);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/space/{spaceId}/file/{fileId}/removeLabel", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity removeLabelFromFile(@PathVariable("fileId") String id, @RequestBody LabelMigrationRepresentation representation){
        labelService.removeLabelFromFile(id, representation);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
