package com.veviosys.vdigit.dashboard.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.veviosys.vdigit.classes.StatisticUser;
import com.veviosys.vdigit.dashboard.models.UserDashboardSettings;
import com.veviosys.vdigit.dashboard.serivces.KPIs;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.services.CostumUserDetails;

import lombok.ToString;

@RequestMapping("api/v1/dashboard")
@RestController
public class DashboardController {
	
	
	@Autowired
	private KPIs KpisService;
	
	public User connectedUser() {
		return ((CostumUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
	}
	
	@GetMapping("flow-in-progress")
	public HashMap<String, List<String>> flowInProgress() {

		return KpisService.getCurrentFlowNormalByName(connectedUser().getUserId());
	}
	
	@GetMapping("flow-in-progress-count")
	public HashMap<String, Long> flowInProgressCount() {

		return KpisService.getCurrentFlowCounts(connectedUser().getUserId());
	}
	
	@GetMapping("flow-done-count")
	public HashMap<String, Long> flowDoneCount() {

		return KpisService.getDoneFlowCounts(connectedUser().getUserId());
	}
	
	
	
	@GetMapping("flow-in-progress-red")
	public HashMap<String, List<String>> flowInProgressRed() {

		return KpisService.getCurrentFlowNormalByNameRed(connectedUser().getUserId());
	}
	
	@GetMapping("flow-in-progress-all")
	public HashMap<String, List<String>> flowInProgressAll() {

		return KpisService.getCurrentFlowNormalByName(connectedUser().getUserId());
	}
	
	@PostMapping("items-by-year")
	public HashMap<String, List<String>> getItemsByType(@RequestBody DashboardFilter filter) {

		System.out.println(filter);
		return KpisService.getItemsByType(filter.tp,filter.showBy,filter.startDate,filter.endDate);
	}
	
	
	
	@PostMapping("done-flow-user")
	public HashMap<String, List<String>> getDoneByUser(@RequestBody DashboardFilter filter) {

		System.out.println(filter);
		
		Long id = Objects.nonNull(filter.tp )? filter.tp  : connectedUser().getUserId();
		return KpisService.getFluxTraiter(id,filter.showBy,filter.startDate,filter.endDate,filter.type);
	}
	
	@PostMapping("done-flow-current-user")
    public HashMap<String, List<String>> getFluxStill(@RequestBody DashboardFilter filter) {

        return KpisService.getFluxStill(connectedUser().getUserId(),filter.showBy,filter.startDate,filter.endDate,filter.type);
    }
	
	@PostMapping("itemsgr-by-year")
	public HashMap<String, List<String>> getItemsByTypeGroup(@RequestBody DashboardFilter filter) {	
		System.out.println(filter);
		return KpisService.getItemsByTypeGroup(filter.tp,filter.showBy,filter.startDate,filter.endDate);
	}
	
	@GetMapping("items-last-q-data")
	public HashMap<String, List<String>> lastQdata() {
		return KpisService.getLastQuaData();
	}
	
	@PostMapping("get-for-table-statistic")
    public List<StatisticUser> getFluxTable(@RequestBody DashboardFilter filter) {
        return KpisService.getFluxTable(connectedUser().getUserId(),filter.showBy,filter.startDate,filter.endDate,filter.type,filter.unit);
    }
    
	
	@PostMapping("user-settings")
	public UserDashboardSettings getItemsByTypeGroup(@RequestBody UserDashboardSettings dashboardSettings) {
		return KpisService.updateCreateUserSettings(connectedUser(), dashboardSettings);
	
	}
	
	
	

}



@ToString
class DashboardFilter{

	public String showBy;
	public LocalDate endDate;
	public LocalDate startDate;
	public Long tp;
	public Long type;
	public Long unit;
}