package cc.iliz.mybatis.shading.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

public class ReflectionUtils {

	private static final Log logger = LogFactory.getLog(ReflectionUtils.class);


	/**
	 * 直接设置对象属性值,无视private/protected修饰符,不经过setter函数.
	 * @param object setting object
	 * @param fieldName setting field 
	 * @param value setting value
	 */
	public static void setFieldValue(final Object object, final String fieldName, final Object value) {
		Field field = getDeclaredField(object, fieldName);

		if (field == null)
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");

		makeAccessible(field);

		try {
			field.set(object, value);
		} catch (IllegalAccessException e) {
			
		}
	}
	
	/**
	 * 直接读取对象属性值,无视private/protected修饰符,不经过getter函数.
	 * @param object getting object
	 * @param fieldName getting field
	 * @return object value of getting field
	 */
	public static Object getFieldValue(final Object object, final String fieldName) {
		Field field = getDeclaredField(object, fieldName);

		if (field == null)
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");

		makeAccessible(field);

		Object result = null;
		try {
			result = field.get(object);
		} catch (IllegalAccessException e) {
			
		}
		return result;
	}

	/**
	 * 直接调用对象方法,无视private/protected修饰符.
	 * @param object invoke object
	 * @param methodName invoke method name
	 * @param parameterTypes invoke method param types
	 * @param parameters invoke method params
	 * @return invoke result
	 * @throws InvocationTargetException invoke error
	 */
	public static Object invokeMethod(final Object object, final String methodName, final Class<?>[] parameterTypes,
			final Object[] parameters) throws InvocationTargetException {
		Method method = getDeclaredMethod(object, methodName, parameterTypes);
		if (method == null)
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + object + "]");

		method.setAccessible(true);

		try {
			return method.invoke(object, parameters);
		} catch (IllegalAccessException e) {
			
		}

		return null;
	}

	/**
	 * 循环向上转型,获取对象的DeclaredField.
	 * @param object getting object
	 * @param fieldName getting field name
	 * @return getting field
	 */
	protected static Field getDeclaredField(final Object object, final String fieldName) {		
		for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				return superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
			}
		}
		return null;
	}

	/**
	 * 循环向上转型,获取对象的DeclaredField.
	 * @param field setting filed
	 */
	protected static void makeAccessible(final Field field) {
		if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
			field.setAccessible(true);
		}
	}

	/**
	 * 循环向上转型,获取对象的DeclaredMethod.
	 * @param object getting object
	 * @param methodName getting method name
	 * @param parameterTypes getting param types
	 * @return method
	 */
	protected static Method getDeclaredMethod(Object object, String methodName, Class<?>[] parameterTypes) {
		for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				return superClass.getDeclaredMethod(methodName, parameterTypes);
			} catch (NoSuchMethodException e) {

			}
		}
		return null;
	}

	/**
	 * 通过反射,获得Class定义中声明的父类的泛型参数的类型.
	 * @param clazz The class to introspect
	 * @return the first generic declaration, or Object.class if cannot be determined
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getSuperClassGenricType(final Class<T> clazz) {
		return getSuperClassGenricType(clazz, 0);
	}

	/**
	 * 通过反射,获得Class定义中声明的父类的泛型参数的类型.
	 * @param clazz clazz The class to introspect
	 * @param index param index 
	 * @return the first generic declaration, or Object.class if cannot be determined
	 */
	@SuppressWarnings("rawtypes")
	public static Class getSuperClassGenricType( final Class clazz, final int index) {

		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			logger.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			logger.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
					+ params.length);
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			logger.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
			return Object.class;
		}

		return (Class) params[index];
	}

	/**
	 * 将反射时的checked exception转换为unchecked exception.
	 * @param e original exception
	 * @return converted exception
	 */
	public static IllegalArgumentException convertToUncheckedException(Exception e) {
		if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
				|| e instanceof NoSuchMethodException)
			return new IllegalArgumentException("Refelction Exception.", e);
		else
			return new IllegalArgumentException(e);
	}
	
	/**
	 * 根据传入的类找到所有的实现接口
	 * @param type 原类
	 * @return 返回类的所有实现接口
	 */
	public static Class<?>[] getAllInterfaces(Class<?> type) {
	    Set<Class<?>> interfaces = new HashSet<Class<?>>();
	    while (type != null) {
	      for (Class<?> c : type.getInterfaces()) {
	          interfaces.add(c);
	      }
	      type = type.getSuperclass();
	    }
	    return interfaces.toArray(new Class<?>[interfaces.size()]);
	  }
}
