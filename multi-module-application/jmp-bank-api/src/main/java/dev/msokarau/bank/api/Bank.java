package dev.msokarau.bank.api;

import dev.msokarau.dto.User;
import dev.msokarau.dto.BankCard;
import dev.msokarau.dto.BankCardType;

public interface Bank {
  public BankCard createBankCard(User user, BankCardType type);
}
