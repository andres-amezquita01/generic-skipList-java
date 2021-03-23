package skipList;

import java.util.Comparator;
import java.util.Random;

/**
 * Estructura de datos SkipList
 * @author Andres Felipe Amezquita Gordillo
 * @param <T> Dato generico.
 */
public class SkipList<T> {
	private SkipNode<T> head;
	private SkipNode<T> tail;
	private int height;
	private Random random;
	private Comparator<T> comparator;
	private static final String MESSAGE_EXCEPTION_DUPLICATE = "Elemento Duplicado";
	private static final String MESSAGE_EXCEPTION_NOT_FOUND = "Elemento No Encontrado";
	/**
	 * 
	 * @param comparator
	 * @param infoMinus
	 * @param infoHigher
	 */
	public SkipList(Comparator<T> comparator) {
		this.comparator = comparator;
		this.height = 0;
		this.random = new Random();
	}
	/**
	 * Busca un nodo que contenga la llave.
	 * @param key
	 * @return
	 */
	public SkipNode<T> searchNode(T key) {
		SkipNode<T> aux = this.head;
		while(aux.below != null) {//mientras haya un nivel inferior
			aux = aux.below;//vamos hundiendonos en la lista
				while(comparator.compare(key, aux.next.key) >= 0){//mientras el siguiente sea menor seguimos saltando
					aux = aux.next;
				}
		}
		
		return aux;
	}
	
	public T search(T key) throws Exception {
		SkipNode<T> aux = searchImplement(key);
		if(aux != null) {
			return aux.key;
		}else {
			throw new Exception(MESSAGE_EXCEPTION_NOT_FOUND);
		}
	}
	
	private SkipNode<T> searchImplement(T key) {
		SkipNode<T> aux = this.head;
		while(aux.below != null) {//mientras haya un nivel inferior
			aux = aux.below;//vamos hundiendonos en la lista
				while(comparator.compare(key, aux.next.key) >= 0){//mientras el siguiente sea menor seguimos saltando
					if(comparator.compare(key, aux.next.key) == 0) { 
						return aux.next;
					}
					aux = aux.next;
				}
		}
		return null;
	}
	/**
	 * Reinicia la SkipList borrando todos los datos.
	 * @param maximun
	 */
	private void resetAllSkipList(T minimun , T maximun) {
		this.head = new SkipNode<>(minimun);
		this.tail = new SkipNode<>(maximun);
		this.height = 0;
		this.head.next = tail;
		this.tail.previous = head;
	}
	
	/**
	 * Añade un elemento cuando es mayor al limite derecho, es
	 * decir cuando key > a tail.key.
	 * @param key
	 */
	private void addToEnd(T key) {
			SkipNode<T> newNode = new SkipNode<T>(key);
			tail.next =newNode;
			this.tail = newNode;
			
			SkipNode<T> starting = this.head;
			SkipNode<T> hightLevel = starting;
			while(hightLevel.below != null) {
				hightLevel = hightLevel.below;
				starting = hightLevel;
			}
			resetAllSkipList(starting.key,key);
			while (starting != null) {
				skipAdd(starting.key);
				starting = starting.next;
			}
	}
	/**
	 * Añade un elemento a la skipList.
	 * @param key Elemento a añadir.
	 * @throws Exception 
	 */
	public void add(T key) throws Exception {
		boolean added = false;
		if(this.head == null) {
			this.head = new SkipNode<>(key);
			this.tail = new SkipNode<>(key);
			this.head.next = tail;
			this.tail.previous = head;
			added = true;
		}else if(comparator.compare(key, this.head.key) <= 0  && added == false) {
			insert(key);
			added = true;
		}else if(comparator.compare(key, this.tail.key) >= 0 && added == false) {
			addToEnd(key);
			added = true;
		}else{
			if(comparator.compare(key, searchNode(key).key) == 0) {
			throw new Exception(MESSAGE_EXCEPTION_DUPLICATE);
			}else {
				skipAdd(key);				
			}
		}
	}
	/**
	 * Inserta un elemento anterior al head.
	 * @param key Elemento a insertar.
	 */
	private void insert(T key) {
			SkipNode<T> starting = this.head;
			SkipNode<T> hightLevel = starting;
			while(hightLevel.below != null) {
				hightLevel = hightLevel.below;
				starting = hightLevel;
			}
			SkipNode<T> newNode = new SkipNode<T>(key);
			newNode.next = starting;
			starting.previous = newNode;
			this.head = newNode;
			SkipNode<T> auxNode = this.head;
			while(auxNode.next != null) {
				auxNode = auxNode.next;
			}
			this.tail = auxNode;
			resetAllSkipList(key,tail.key );
			SkipNode<T> auxiliar = starting;
			while(auxiliar.next != null) {
				skipAdd(auxiliar.key);
				auxiliar = auxiliar.next;
			}
	}
	/**
	 * Añade un elemento en medio de la skiplist.
	 * @param key Elemento a añadir
	 * @return 
	 */
	private SkipNode<T> skipAdd(T key) {
		SkipNode<T> position = searchNode(key);//buscamos el nodo donde vamos a insertar
		SkipNode<T> newNode;
		int level = -1;
		if(comparator.compare(position.key, key) == 0) {//el elemento esta duplicado y no podemos insertarlo.
			return position;
		}
		do {
			level++;//siempre se va a inicializar como 0 antes de mandarlo al canIncreaseLevel(level)
			canIncreaseLevel(level);
			newNode = position;
			
			while(position.above == null) {
				position = position.previous;
			}
			position = position.above;
			newNode = insertAfterAbove(position, newNode, key);
		}while(random.nextBoolean() == true	);
		return newNode;
	}
	
	/**
	 * Valida si podemos o no incrementar el nivel.
	 */
	private void canIncreaseLevel(int level) {
		if(level >=	 height) {
			height++;
			addEmptyLevel();
		}
	}
	
	/**
	 * Crea un nuevo nivel de la lista con su limite izquierdo y derecho.
	 */
	private void addEmptyLevel() {
		SkipNode<T> newHeadNode = new SkipNode<>(this.head.key);
		SkipNode<T> newTailNode = new SkipNode<>(this.tail.key);
		newHeadNode.next = newTailNode;
		newHeadNode.below = head;
		newTailNode.previous = newHeadNode;
		newTailNode.below = tail; 
		
		head.above = newHeadNode;//apilamos sobre la cabeza la nueva lista.
		tail.above = newTailNode;//la nueva cima sera la nueva lista.
		
		head = newHeadNode;//reemplazamos la nueva cabeza o nivel 0 de la lista.
		tail = newTailNode;//reemplazamos la nueva cima de nuestra lista.
	}
	
	private SkipNode<T> insertAfterAbove(SkipNode<T> position, SkipNode<T> node, T key) {
		SkipNode<T> newNode = new SkipNode<>(key);
		SkipNode<T> nodeBeforeNewNode = position.below.below;
		
		setBeforeAndAfterReferences(node, newNode);
		setAboveAndBelowReferences(position, key, newNode, nodeBeforeNewNode);
		return newNode;
	}
	
	private void setBeforeAndAfterReferences(SkipNode<T> node, SkipNode<T> newNode) {
		newNode.next = node.next;
		newNode.previous = node;
		node.next.previous = newNode;
		node.next = newNode;
	}
	
	private void setAboveAndBelowReferences(SkipNode<T> position, T key, SkipNode<T> newNode, SkipNode<T> nodeBeforeNewNode) {
		if(nodeBeforeNewNode != null) {
			while(true) {
				if(comparator.compare(nodeBeforeNewNode.next.key, key) != 0) {
					nodeBeforeNewNode = nodeBeforeNewNode.next;
				}else {
					break;
				}
			}
			newNode.below = nodeBeforeNewNode.next;
			nodeBeforeNewNode.next.above = newNode;
		}
		
		if(position != null) {
			if(comparator.compare(position.next.key, key) == 0) {
				newNode.above = position.next;
			}
		}
	}
	
	/**
	 * Elimina un elemento de la lista.
	 * @param key
	 * @return
	 */
	public SkipNode<T> remove(T key) {
		SkipNode<T> nodeToRemove = searchNode(key);
		if(comparator.compare(nodeToRemove.key, key) != 0) {
			return null;
		}
		removeReferencesToNode(nodeToRemove);
		while(nodeToRemove != null) {
			removeReferencesToNode(nodeToRemove);
			if(nodeToRemove.above != null) {
				nodeToRemove = nodeToRemove.above;
			}else{
				break;
			}
		}
		return nodeToRemove;
	}
	
	private void removeReferencesToNode(SkipNode<T> nodeToRemove) {
		SkipNode<T> afterNodeToRemove = nodeToRemove.next;
		SkipNode<T> beforeNodeToRemove = nodeToRemove.previous;
		
		beforeNodeToRemove.next = afterNodeToRemove;
		afterNodeToRemove.previous = beforeNodeToRemove;
	}
	
	public void showMySkipList() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\nSkiptList starting with top-left most node.\n");
		SkipNode<T> starting = head;
		SkipNode<T> hightLevel = starting;
		int level = height;
		while(hightLevel != null) {
			stringBuilder.append("\nLevel: " + level + "\n");
			
			while(starting != null) {
				stringBuilder.append(starting.key);
				if(starting.next != null) {
					stringBuilder.append(" : ");
				}
				starting = starting.next;
			}
			hightLevel = hightLevel.below;
			starting = hightLevel;
			level --;
		}
		System.out.println(stringBuilder.toString());
	}
}
