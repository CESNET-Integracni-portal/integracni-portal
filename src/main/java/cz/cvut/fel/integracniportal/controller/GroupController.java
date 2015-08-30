package cz.cvut.fel.integracniportal.controller;

import cz.cvut.fel.integracniportal.model.Group;
import cz.cvut.fel.integracniportal.representation.GroupMemberIdRepresentation;
import cz.cvut.fel.integracniportal.representation.GroupRepresentation;
import cz.cvut.fel.integracniportal.representation.NameRepresentation;
import cz.cvut.fel.integracniportal.service.GroupService;
import cz.cvut.fel.integracniportal.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/rest")
public class GroupController extends AbstractController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserDetailsService userService;

    /**
     * Get all user groups.
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/group", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getUserGroups() {
        List<Group> groups = groupService.getUserGroups(userService.getCurrentUser());
        List<GroupRepresentation> representations = new ArrayList<GroupRepresentation>();
        for (Group group : groups) {
            representations.add(new GroupRepresentation(group));
        }
        return new ResponseEntity<Object>(representations, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/group", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity create(@RequestBody GroupRepresentation representation) {
        Group userGroup = groupService.createUserGroup(userService.getCurrentUser(), representation);
        return new ResponseEntity(new GroupRepresentation(userGroup), HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/group/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") String groupId) {
        groupService.deleteGroup(groupId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/group/{id}/nameChange", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity changeName(@PathVariable("id") String groupId, @RequestBody NameRepresentation representation) {
        groupService.renameGroup(groupId, representation.getName());
        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/group/{id}/addMember", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity addMember(@PathVariable("id") String groupId, @RequestBody GroupMemberIdRepresentation representation) {
        groupService.addMember(groupId, representation.getMemberId());
        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/v0.2/group/{id}/removeMember", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity removeMember(@PathVariable("id") String groupId, @RequestBody GroupMemberIdRepresentation representation) {
        groupService.removeMember(groupId, representation.getMemberId());
        return new ResponseEntity(HttpStatus.OK);
    }

}
