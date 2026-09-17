package com.github.lvpasqualini.ms.pagamento.service;

import com.github.lvpasqualini.ms.pagamento.dto.PagamentoDTO;
import com.github.lvpasqualini.ms.pagamento.dto.PagamentoRequestDTO;
import com.github.lvpasqualini.ms.pagamento.dto.PagamentoResponseDTO;
import com.github.lvpasqualini.ms.pagamento.entities.Pagamento;
import com.github.lvpasqualini.ms.pagamento.exceptions.ResourceNotFoundException;
import com.github.lvpasqualini.ms.pagamento.repositories.PagamentoRepository;
import com.github.lvpasqualini.ms.pagamento.tests.Factory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class PagamentoServiceTest {
    @Mock
    private PagamentoRepository pagamentoRepository;

    @InjectMocks
    private PagamentoService pagamentoService;

    private Long existingId;
    private Long nonExistingId;

    private Pagamento pagamento;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = Long.MAX_VALUE;

        pagamento = Factory.createPagamento();
    }

    @Test
    void deletePagamentoByIdShouldDeleteWhenIdExists() {
        Mockito.when(pagamentoRepository.existsById(existingId)).thenReturn(true);
        pagamentoService.deleteById(existingId);

        Mockito.verify(pagamentoRepository).existsById(existingId);
        Mockito.verify(pagamentoRepository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    void deletePagamentoByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Mockito.when(pagamentoRepository.existsById(nonExistingId)).thenReturn(false);
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> {
                    pagamentoService.deleteById(nonExistingId);
                });
        Mockito.verify(pagamentoRepository).existsById(nonExistingId);
        Mockito.verify(pagamentoRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }

    @Test
    void findPagamentoByIdShouldReturnPagamentoDTOWhenIdExists() {

        // Arrange
        Mockito.when(pagamentoRepository.findById(existingId))
                .thenReturn(Optional.of(pagamento));

        // Act
        PagamentoResponseDTO result = pagamentoService.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(pagamento.getId(), result.getId());
        Assertions.assertEquals(pagamento.getValor(), result.getValor());

        Mockito.verify(pagamentoRepository).findById(existingId);
        Mockito.verifyNoMoreInteractions(pagamentoRepository);
    }

    @Test
    void givenValidParamsAndIdIsNull_whenSave_thenShouldPersistPagamento() {
        //Arrange
        pagamento.setId(null);
        Mockito.when(pagamentoRepository.save(any(Pagamento.class)))
                .thenReturn(pagamento);
        PagamentoRequestDTO inputDto = new PagamentoRequestDTO(pagamento);
        //Act
        PagamentoResponseDTO result = pagamentoService.save(inputDto);
        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(pagamento.getId(),result.getId());
        //Verify
        Mockito.verify(pagamentoRepository).save(any(Pagamento.class));
        Mockito.verifyNoMoreInteractions(pagamentoRepository);
    }

    @Test
    void updatePagamentoShouldReturnPagamentoDTOWhenIdExists() {
        //Arrange
        Long id = pagamento.getId();
        Mockito.when(pagamentoRepository.getReferenceById(id)).thenReturn(pagamento);
        Mockito.when(pagamentoRepository.save(any(Pagamento.class))).thenReturn(pagamento);
        //Act
        PagamentoResponseDTO result = pagamentoService.update(id,new PagamentoRequestDTO(pagamento));
        //Assert e Verify
        Assertions.assertNotNull(result);
        Assertions.assertEquals(id, result.getId());
        Assertions.assertEquals(pagamento.getValor(), result.getValor());
        Mockito.verify(pagamentoRepository).getReferenceById(id);
        Mockito.verify(pagamentoRepository).save(Mockito.any(Pagamento.class));
        Mockito.verifyNoMoreInteractions(pagamentoRepository);
    }

    @Test
    void updatePagamentoShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        // Quando o método getReferenceById do pagamentoRepository é chamado com o ID não existente,
        // ele lança uma exceção EntityNotFoundException
        Mockito.when(pagamentoRepository.getReferenceById(nonExistingId))
                .thenThrow(EntityNotFoundException.class);

        // Criação do objeto inputDto que será passado como parâmetro para o método de atualização
        PagamentoRequestDTO inputDto = new PagamentoRequestDTO(pagamento);

        // Assegura que, quando o método updatePagamento for chamado, uma exceção do tipo ResourceNotFoundException
        // será lançada
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> pagamentoService.update(nonExistingId, inputDto));

        // Verifica se o método getReferenceById foi chamado com o ID não existente
        Mockito.verify(pagamentoRepository).getReferenceById(nonExistingId);

        // Verifica que o método save NUNCA foi chamado no repositório (já que o ID não existe)
        Mockito.verify(pagamentoRepository, Mockito.never()).save(Mockito.any(Pagamento.class));

        // Verifica que não houve mais interações com o pagamentoRepository além das verificações anteriores
        Mockito.verifyNoMoreInteractions(pagamentoRepository);
    }
}