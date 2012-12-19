package com.cinchapi.concourse.data;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.cinchapi.commons.annotations.UnitTested;
import com.cinchapi.concourse.annotations.DataType;
import com.cinchapi.concourse.annotations.Immutable;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Abstract implementation of the {@link Property} interface.
 * @author jnelson
 *
 * @param <T> - the <code>value</code> type
 */
@Immutable
@DataType("abstract")
@UnitTested(PropertyTest.class)
public abstract class AbstractProperty<T> implements Property<T>{
	
	private final String key;
	private final T value;
	
	/* lazy initialization */
	private String type; 
	private transient int hashCode;
	
	private static final Gson json = new Gson();
	
	/**
	 * Create a new {@link AbstractProperty} with a<code>key</code> and <code>value</code>.
	 * @param key
	 * @param value
	 */
	public AbstractProperty(String key, T value){
		this.key = key.toLowerCase().replace(" ", "_");
		this.value = value;
		this.type = getType();
	}
	
	@Override
	public String getKey() {
		return key;
	}

	@Override
	public T getValue() {
		return value;
	}
	
	@Override
	public String getType(){
		if(type == null){
			this.type = this.getClass().getAnnotation(DataType.class).value();
		}
		return type;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object obj){
		if(obj == null){
			return false;
		}
		else if(obj == this){
			return true;
		}
		else if(obj.getClass() != this.getClass()){
			return false;
		}
		else{
			AbstractProperty other = (AbstractProperty) obj;
			return new EqualsBuilder().append(key, other.key)
					.append(value, other.value)
					.append(getType(), other.getType())
					.isEquals();
		}
	}

	@Override
	public int hashCode(){
		if(hashCode == 0){
			hashCode = new HashCodeBuilder().append(getKey()).
					append(getValue()).
					append(getType())
					.toHashCode();
		}
		return hashCode;
	}
	
	@Override
	public String toString(){
		JsonObject object = new JsonObject();
		object.addProperty("key", key);
		object.addProperty("value", value.toString());
		object.addProperty("type", getType());
		return json.toJson(object);
	}

}
