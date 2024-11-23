package com.veviosys.vdigit.controllers;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.veviosys.vdigit.models.Attribute;
import com.veviosys.vdigit.models.ElementTypeGroup;
import com.veviosys.vdigit.services.ElementTypeGroupService;


@RestController
@RequestMapping("api/v1/elementtypegroup")
public class ElementTypeGroupController {

	@Autowired
	private ElementTypeGroupService elementTypeGroupService;

	@GetMapping
	public ResponseEntity<Page<ElementTypeGroup>> getAll(Pageable pageable,@RequestParam(name = "q") String q) {
		return ResponseEntity.ok(elementTypeGroupService.getAll(pageable,q));

	}
	
	@GetMapping("/withoutpage")
    public ResponseEntity<List<ElementTypeGroup>> getAllWithoutPage() {
        return ResponseEntity.ok(elementTypeGroupService.getAllWithoutPage());

    }

	@GetMapping("{id}/commonattrs")
	public ResponseEntity<List<Attribute>> getCommonAttrs(@PathVariable Long id) {

		return ResponseEntity.ok(elementTypeGroupService.getCommonAttrs(id));

	}

	@PostMapping("/commonattrs/types")
	public ResponseEntity<List<Attribute>> getCommonAttrsByTypes(@RequestBody List<Long> id) throws SQLException {
		return ResponseEntity.ok(elementTypeGroupService.getCommonAttrsByTypes(id));

	}

	@PostMapping
	public ResponseEntity<ElementTypeGroup> addTypeGroup(@RequestBody ElementTypeGroup elementTypeGroup) {

		return ResponseEntity.ok(elementTypeGroupService.add(elementTypeGroup));

	}

	@PostMapping("update")
	public ResponseEntity<ElementTypeGroup> updateTypeGroup(@RequestBody ElementTypeGroup elementTypeGroup) {

		return ResponseEntity.ok(elementTypeGroupService.update(elementTypeGroup));

	}

	

	@GetMapping("{id}/enableautonum")
	public ResponseEntity<ElementTypeGroup> enableAutoNum(@PathVariable Long id) {

		return ResponseEntity.ok(elementTypeGroupService.enalbeAutoNum(elementTypeGroupService.getOne(id)));

	}

	@GetMapping("{id}/disableautonum")
	public ResponseEntity<ElementTypeGroup> disableAutoNum(@PathVariable Long id) {

		return ResponseEntity.ok(elementTypeGroupService.disalbeAutoNum(elementTypeGroupService.getOne(id)));

	}

	@GetMapping("{id}/init")
	public ResponseEntity<ElementTypeGroup> initAuto(@PathVariable Long id) {

		return ResponseEntity.ok(elementTypeGroupService.resetSeq(elementTypeGroupService.getOne(id)));

	}

	@DeleteMapping("{id}")
	public ResponseEntity<ElementTypeGroup> deleteTypeGroup(@PathVariable Long id) {

		elementTypeGroupService.deleteElementGroup(elementTypeGroupService.getOne(id));

		return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);

	}

}
