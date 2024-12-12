package com.websales.admin.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.websales.common.entity.Country;
import com.websales.common.entity.State;
import com.websales.common.entity.StateDTO;

@RestController
public class StateRestController {

	
	@Autowired 
	private StateRepository repo;

	@GetMapping("/states/list_by_country/{id}")
	public List<StateDTO> listByCountry(@PathVariable("id") Integer countryId) {
		
	
		List<State> listStates = repo.findByCountryOrderByNameAsc(new Country(countryId));
		
		
		List<StateDTO> result = new ArrayList<>();

		for (State state : listStates) {
			result.add(new StateDTO(state.getId(), state.getName()));
		}
		

		return result;
	}

	@PostMapping("/states/save")
	public String save(@RequestBody State state) {
		State savedState = repo.save(state);
		return String.valueOf(savedState.getId());
	}

	@DeleteMapping("/states/delete/{id}")
	public void delete(@PathVariable("id") Integer id) {
		repo.deleteById(id);
	}
	
//	@PostMapping("/states/check_unique")
//	@ResponseBody
//	public String checkUnique(@RequestBody Map<String,String> data) {
//		
//		String name = data.get("name");
//	
//		State countryByName = repo.findByName(name);
//		boolean isCreatingNew = (countryByName.getId() != null ? true : false);
//		
//		if (isCreatingNew) {
//			if (countryByName != null) return "Duplicate";
//		} else {
//			if (countryByName != null && countryByName.getId() != null) {
//				return "Duplicate";
//			}
//		}
//		
//		return "OK";
//	}
}