package domain.models;

import java.util.Date;

public class BirthdayBonusSubscriber extends MSISDN {

	private String name;
	private int language, bonus;
	private long aspu;
	private Date birth_date, last_update_time, bonus_expires_in;

	public BirthdayBonusSubscriber() {
		super();
	}

	public BirthdayBonusSubscriber(int id, String msisdn, String name, int language, Date birth_date) {
		super(id, msisdn);
		this.name = name;
		this.language = language;
		this.birth_date = birth_date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLanguage() {
		return language;
	}

	public void setLanguage(int language) {
		this.language = language;
	}

	public Date getBirth_date() {
		return birth_date;
	}

	public void setBirth_date(Date birth_date) {
		this.birth_date = birth_date;
	}

	public long getAspu() {
		return aspu;
	}

	public void setAspu(long aspu) {
		this.aspu = aspu;
	}

	public int getBonus() {
		return bonus;
	}

	public void setBonus(int bonus) {
		this.bonus = bonus;
	}

	public Date getLast_update_time() {
		return last_update_time;
	}

	public void setLast_update_time(Date last_update_time) {
		this.last_update_time = last_update_time;
	}

	public Date getBonus_expires_in() {
		return bonus_expires_in;
	}

	public void setBonus_expires_in(Date bonus_expires_in) {
		this.bonus_expires_in = bonus_expires_in;
	}

	public int hashCode() {
		return getValue().hashCode();
	}

	public boolean equals (Object pp) {
		try {
			BirthdayBonusSubscriber p = (BirthdayBonusSubscriber) pp;

			if(this.getValue().equals(p.getValue())) {
				/*if(p.last_update_time != null) {
					if((this.last_update_time == null) || (p.last_update_time.after(this.last_update_time))) {
						this.last_update_time = p.last_update_time;
						this.name = p.name;
						this.birth_date = p.birth_date;
						this.language = p.language;
					}
				}*/

				if(p.aspu > 0) {
					this.aspu = Math.max(this.aspu, p.aspu);
				}

				return true;
			}

		} catch(Throwable th) {

		}

		return false;
	}

}
