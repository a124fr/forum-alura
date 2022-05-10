package br.com.alura.forum.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.forum.controller.dto.DetalhesDoTopicoDto;
import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.controller.form.AtualizacaoTopicoForm;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;

@RestController
@RequestMapping("/topicos")
public class TopicoController {
	
	@Autowired
	private TopicoRepository topicoRepository;
	
	@Autowired
	private CursoRepository cursoRepository;
	
//	@RequestMapping(method = RequestMethod.POST, value = "/topicos")
//	@GetMapping
//	public List<TopicoDto> lista(String nomeCurso) {
//		if (nomeCurso == null) {
//			return TopicoDto.converter(this.topicoRepository.findAll());
//		} else {
//			return TopicoDto.converter(this.topicoRepository.findByCursoNome(nomeCurso));
//		}		
//	}
	
//	@GetMapping
//	public Page<TopicoDto> lista(@RequestParam(required = false) String nomeCurso, 
//			@RequestParam int pagina, @RequestParam int qtd, @RequestParam String ordenacao) {		
//		Pageable paginacao = PageRequest.of(pagina, qtd, Direction.ASC, ordenacao);		
//		if (nomeCurso == null) {
//			return TopicoDto.converter(this.topicoRepository.findAll(paginacao));
//			 
//		} else {
//			return TopicoDto.converter(this.topicoRepository.findByCursoNome(nomeCurso, paginacao));
//		}		
//	}
	
	@GetMapping
	public Page<TopicoDto> lista(@RequestParam(required = false) String nomeCurso, 
		@PageableDefault(sort = "id", direction = Direction.DESC, page = 0, size = 10)	Pageable paginacao) {
		if (nomeCurso == null) {
			return TopicoDto.converter(this.topicoRepository.findAll(paginacao));
			 
		} else {
			return TopicoDto.converter(this.topicoRepository.findByCursoNome(nomeCurso, paginacao));
		}		
	}
	
//	@RequestMapping(method = RequestMethod.POST, value = "/topicos")
	@Transactional
	@PostMapping
	public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm form, UriComponentsBuilder uriBuilder) {		
		Topico topico = form.converter(this.cursoRepository);
		this.topicoRepository.save(topico);
		
		URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
		return ResponseEntity.created(uri).body(new TopicoDto(topico));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<DetalhesDoTopicoDto> detalhar(@PathVariable Long id) {
		Optional<Topico> optionalTopico = this.topicoRepository.findById(id);
		if (optionalTopico.isPresent()) {
			return ResponseEntity.ok(new DetalhesDoTopicoDto(optionalTopico.get()));			
		}
		
		return ResponseEntity.notFound().build();
	}
	
	@Transactional
	@PutMapping("/{id}")
	public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoForm form) {
		Optional<Topico> optionalTopico = this.topicoRepository.findById(id);
		if (optionalTopico.isPresent()) {
			Topico topico = form.atualizar(id, topicoRepository);		
			return ResponseEntity.ok(new TopicoDto(topico));
		}
		
		return ResponseEntity.notFound().build();
	}
	
	@Transactional
	@DeleteMapping("/{id}")
	public ResponseEntity<?> excluir(@PathVariable Long id) {
		Optional<Topico> optionalTopico = this.topicoRepository.findById(id);
		if (optionalTopico.isPresent()) {
			this.topicoRepository.deleteById(id);		
			return ResponseEntity.ok().build();
		}
		
		return ResponseEntity.notFound().build();
	}
}
