package com.bos.service.impl;

import com.bos.constant.TransTypeConstant;
import com.bos.exception.BadRequestException;
import com.bos.exception.ForbiddenException;
import com.bos.exception.ServiceException;
import com.bos.model.dto.ResponseDto;
import com.bos.model.dto.TransReqDto;
import com.bos.model.dto.TransResDto;
import com.bos.model.entity.BosBalance;
import com.bos.repository.BosBalanceRepository;
import com.bos.repository.BosCounterRepository;
import com.bos.repository.BosHistoryRepository;
import com.bos.service.TransactionService;
import com.bos.util.DateUtil;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final BosBalanceRepository bosBalanceRepository;
    private final BosCounterRepository bosCounterRepository;
    private final BosHistoryRepository bosHistoryRepository;

    private static final String SUCCESS = "Sukses proses data";

    @Override
    @Transactional
    public ResponseDto setor(TransReqDto dto) {
        try {
            requestValidation(dto, TransTypeConstant.DP);
            int result = balanceTrans(dto.getAccountId(), dto.getCurrencyId(), dto.getAmount());
            if (result != 1) {
                throw new BadRequestException("Gagal melakukan penyetoran dana");
            }

            String transId = getTransId();
            addHistory(transId, dto.getAccountId(), dto.getCurrencyId(), dto.getAmount(), TransTypeConstant.DP);

            return new ResponseDto(201, SUCCESS, new TransResDto(transId, dto.getAccountId(), null,
                    dto.getCurrencyId(), dto.getAmount(), TransTypeConstant.DP));
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseDto tarik(TransReqDto dto) {
        try {
            requestValidation(dto, TransTypeConstant.WD);
            if (!amountCheck(dto)) {
                throw new ForbiddenException("Saldo tidak mencukupi untuk dilakukan penarikan dana");
            }

            dto.setAmount(dto.getAmount().negate());
            int result = balanceTrans(dto.getAccountId(), dto.getCurrencyId(), dto.getAmount());
            if (result != 1) {
                throw new BadRequestException("Gagal melakukan penarikan dana");
            }

            String transId = getTransId();
            addHistory(transId, dto.getAccountId(), dto.getCurrencyId(), dto.getAmount(), TransTypeConstant.WD);

            return new ResponseDto(201, SUCCESS, new TransResDto(transId, dto.getAccountId(), null,
                    dto.getCurrencyId(), dto.getAmount(), TransTypeConstant.WD));
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseDto transfer(TransReqDto dto) {
        try {
            requestValidation(dto, TransTypeConstant.TR);
            if (!amountCheck(dto)) {
                throw new ForbiddenException("Saldo tidak mencukupi untuk dilakukan pemindahan dana");
            }

            int result = balanceTrans(dto.getAccountDest(), dto.getCurrencyId(), dto.getAmount());
            if (result != 1) {
                throw new BadRequestException("Gagal melakukan pemindahan dana");
            }

            String transId = getTransId();
            addHistory(transId, dto.getAccountId(), dto.getCurrencyId(), dto.getAmount(), TransTypeConstant.TR);

            dto.setAmount(dto.getAmount().negate());
            result = balanceTrans(dto.getAccountId(), dto.getCurrencyId(), dto.getAmount());
            if (result != 1) {
                throw new BadRequestException("Gagal melakukan pemindahan dana");
            }

            transId = getTransId();
            addHistory(transId, dto.getAccountDest(), dto.getCurrencyId(), dto.getAmount(), TransTypeConstant.TR);

            return new ResponseDto(201, SUCCESS, new TransResDto(transId, dto.getAccountId(), dto.getAccountDest(),
                    dto.getCurrencyId(), dto.getAmount(), TransTypeConstant.TR));
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseDto history(String accountId, String from, String to) {
        try {
            if (!DateUtil.isDateAfterOrEqual(from, to)) {
                throw new BadRequestException("From date tidak boleh lebih besar dari To date");
            }

            return new ResponseDto(200, SUCCESS, bosHistoryRepository.searchHistory(
                    "%" + accountId + "%",
                    StringUtils.isEmpty(from) ? "" : String.format("%s 00:00:00", from),
                    StringUtils.isEmpty(to) ? "" : String.format("%s 23:59:59", to)
            ));
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    private int balanceTrans(String accountId, String currencyId, BigDecimal amount) {
        return bosBalanceRepository.getBalance(accountId, currencyId) != null
                ? bosBalanceRepository.updateBalance(accountId, currencyId, getBalance(accountId, currencyId).add(amount))
                : bosBalanceRepository.insertBalance(accountId, currencyId, amount);
    }

    private boolean amountCheck(TransReqDto dto) {
        return getBalance(dto.getAccountId(), dto.getCurrencyId()).compareTo(dto.getAmount()) >= 0;
    }

    private void requestValidation(TransReqDto dto, String type) {
        if (!dto.getCurrencyId().equals("IDR") && !dto.getCurrencyId().equals("SGD")) {
            throw new ForbiddenException("Mata uang yang diizinkan untuk bertransaksi hanya IDR dan SGD");
        }

        if (StringUtils.isEmpty(dto.getAccountId())) {
            throw new BadRequestException("Id akun tidak boleh kosong");
        }

        if (StringUtils.isEmpty(dto.getCurrencyId())) {
            throw new BadRequestException("Tipe mata uang tidak boleh kosong");
        }

        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount tidak boleh kosong");
        }

        if (type.equalsIgnoreCase(TransTypeConstant.TR) && StringUtils.isEmpty(dto.getAccountDest())) {
            throw new BadRequestException("Akun tujuan tidak boleh kosong");
        }

        if (type.equalsIgnoreCase(TransTypeConstant.TR) && dto.getAccountId().equals(dto.getAccountDest())) {
            throw new BadRequestException("Akun tujuan tidak boleh kosong");
        }
    }

    private BigDecimal getBalance(String accountId, String currencyId) {
        return bosBalanceRepository.getBalanceAmount(accountId, currencyId);
    }

    private String getTransId() {
        Long nextNumber = bosCounterRepository.getLastCounterNumber() + 1;
        String transId = String.format("%s-00000.%05d", java.time.LocalDate.now().toString()
                .replace("-", ""), nextNumber);
        bosCounterRepository.updateLastCounter(nextNumber);
        return transId;
    }

    private void addHistory(String transId, String accountId, String currencyId, BigDecimal amount, String note) {
        bosHistoryRepository.insertHistory(transId, accountId, currencyId, amount, note);
    }
}