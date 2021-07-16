package homepage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import base.BaseTests;
import pages.CarrinhoPage;
import pages.CheckoutPage;
import pages.LoginPage;
import pages.ModalProdutoPage;
import pages.PedidoPage;
import pages.ProdutoPage;
import util.Funcoes;

public class HomePageTests extends BaseTests {

	@Test
	public void testContarProdutos_oitoProdutosDiferentes() {
		carregarPaginaInicial();
		assertThat(homePage.contarProdutos(), is(8));
	}

	@Test
	public void testValidarCarrinhoZerado_ZeroItensNoCarrinho() {
		int produtosNoCarrinho = homePage.obterQuantidadeProdutosNoCarrinho();
		assertThat(produtosNoCarrinho, is(0));
	}

	String nomeProduto_HomePage;
	String precoProduto_HomePage;

	String nomeProduto_ProdutoPage;
	String precoProduto_ProdutoPage;

	ProdutoPage produtoPage;

	@Test
	public void testValidarDetalhesDoProduto_DescricaoEValorIguais() {
		int indice = 0;
		nomeProduto_HomePage = homePage.obterNomeProduto(indice);
		precoProduto_HomePage = homePage.obterPrecoProduto(indice);

		System.out.println(nomeProduto_HomePage);
		System.out.println(precoProduto_HomePage);

		produtoPage = homePage.clicarProduto(indice);

		nomeProduto_ProdutoPage = produtoPage.obterNomeProduto();
		precoProduto_ProdutoPage = produtoPage.obterprecoProduto();

		System.out.println(nomeProduto_ProdutoPage);
		System.out.println(precoProduto_ProdutoPage);

		assertThat(nomeProduto_HomePage.toUpperCase(), is(nomeProduto_ProdutoPage.toUpperCase()));
		assertThat(precoProduto_HomePage, is(precoProduto_ProdutoPage));
	}

	LoginPage loginPage;
	
	

	@Test
	public void testLoginComSucesso_UsuarioLogado() {
		// Clicar no bot�o Sign In na home page
		loginPage = homePage.clicarBotaoSignIn();

		// Preencher usu�rio e senha
		loginPage.preencherEmail("testeWell@teste.com");
		loginPage.preencherPassword("123456");

		// Clicar no bot�o Sign In na para logar
		loginPage.clicarBotaoSignIn();

		// Validar se o usu�rio est� logado de fato
		assertThat(homePage.estaLogado("TesteWell Silva"), is(true));

		// Retornar a p�gina inicial
		carregarPaginaInicial();
	}
	
	//Utilizando massa de dados no teste
	@ParameterizedTest
	@CsvFileSource(resources = "/massaTeste_login.csv", numLinesToSkip = 1, delimiter = ';')
	public void testLogin_UsuarioLogadoComDadosValidos(String nomeTeste, String email, String password, String nomeUsuario, String resultado) {
		// Clicar no bot�o Sign In na home page
				loginPage = homePage.clicarBotaoSignIn();

				// Preencher usu�rio e senha
				loginPage.preencherEmail(email);
				loginPage.preencherPassword(password);

				// Clicar no bot�o Sign In na para logar
				loginPage.clicarBotaoSignIn();
				
				boolean esperado_loginOK;
				if(resultado.equals("positivo"))
					esperado_loginOK = true;
				else
					esperado_loginOK = false;

				// Validar se o usu�rio est� logado de fato
				assertThat(homePage.estaLogado(nomeUsuario), is(esperado_loginOK));
				
				capturarTela(nomeTeste, resultado);
				
				if(esperado_loginOK)
					homePage.clicarBotaoSignOut();

				// Retornar a p�gina inicial
				carregarPaginaInicial();
	}

	ModalProdutoPage modalProdutoPage;

	String tamanhoProduto = "M";
	String corProduto = "Black";
	int quantidadeProduto = 2;

	@Test
	public void testIncluirProdutoNoCarrinho_ProdutoIncluidoComSucesso() {

		// Pr�-Condi��o
		// Verificar se est� logado
		if (!homePage.estaLogado("TesteWell Silva")) {
			testLoginComSucesso_UsuarioLogado();
		}

		// Teste
		// Selecionar produto
		testValidarDetalhesDoProduto_DescricaoEValorIguais();

		// Selecionar tamanho
		List<String> listaOpcoes = produtoPage.obterOpcoesSelecionadas();

		System.out.println(listaOpcoes.get(0));
		System.out.println("Tamanho da lista: " + listaOpcoes.size());

		produtoPage.selecionarOpcaoDropDown(tamanhoProduto);

		listaOpcoes = produtoPage.obterOpcoesSelecionadas();

		System.out.println(listaOpcoes.get(0));
		System.out.println("Tamanho da lista: " + listaOpcoes.size());

		// Selecionar cor
		produtoPage.selecionarCorPreta();

		// Selecionar quantidade
		produtoPage.alterarQuantidade(quantidadeProduto);

		// Adicionar no carrinho
		modalProdutoPage = produtoPage.clicarBotaoAddToCart();
		
		
		
		//Valida��es
				
		assertThat(Funcoes.removeCaracterEspecial(modalProdutoPage.obterMensagemProdutoAdicionado()), is ("Product successfully added to your shopping cart"));
		
		//Podemos comparar a mensagem sem o tratamento realizado na classe ModalProdutoPage	desta forma
		//assertTrue(modalProdutoPage.obterMensagemProdutoAdicionado().endsWith("Product successfully added to your shopping cart"));
		
		assertThat(modalProdutoPage.obterDescricaoProduto().toUpperCase(), is(nomeProduto_ProdutoPage.toUpperCase()));
		
		String precoProdutoString = modalProdutoPage.obterPrecoProduto();
		precoProdutoString = precoProdutoString.replace("$", "");
		Double precoProduto = Double.parseDouble(precoProdutoString);
		
		
		assertThat(modalProdutoPage.obterTamanhoProduto(), is (tamanhoProduto));
		assertThat(modalProdutoPage.obterCorProduto(), is(corProduto));
		assertThat(modalProdutoPage.obterQuantidadeProduto(), is(Integer.toString(quantidadeProduto)));	
		
		String subtotalString = modalProdutoPage.obterSubtotal();
		subtotalString = subtotalString.replace("$", "");
		Double subtotal = Double.parseDouble(subtotalString);
				
		
		Double subtotalCalculado = quantidadeProduto * precoProduto;
		
		assertThat(subtotal, is (subtotalCalculado));
		
	}
	
	CarrinhoPage carrinhoPage;
	//Valores esperados
	String esperado_NomeProduto = "Hummingbird printed t-shirt";
	Double esperado_PrecoProduto = 19.12;
	String esperado_TamanhoProduto = "M";
	String esperado_CorProduto = "Black";
	int esperado_QuantidadeProduto = 2;
	Double esperado_SubtotalProduto = esperado_PrecoProduto * esperado_QuantidadeProduto;
	
	int esperado_NumeroItensTotal = esperado_QuantidadeProduto;
	Double esperado_SubtotalTotal = esperado_SubtotalProduto;
	Double esperado_ShippingTotal = 7.00;
	Double esperado_TotalTaxExclTotal = esperado_SubtotalTotal + esperado_ShippingTotal;
	Double esperado_TotalTaxIncTotal = esperado_TotalTaxExclTotal;
	Double esperado_TaxesTotal = 0.00;
	
	String esperado_nomeCliente = "TesteWell Silva";
	
	@Test
	public void testIrParaCarrinho_InformacoesPersistidas() {
		//--Pr�-condi��es
		//Produto inclu�do na tela ModalProdutoPage
		testIncluirProdutoNoCarrinho_ProdutoIncluidoComSucesso();
		
		carrinhoPage = modalProdutoPage.clicarBotaoProceedToCheckout();
		
		//Teste
		
		//Validar todos elementos da tela
		
		System.out.println("TELA DO CARRINHO");
		
		System.out.println(carrinhoPage.obter_NomeProduto());
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_PrecoProduto()));
		System.out.println(carrinhoPage.obter_TamanhoProduto());
		System.out.println(carrinhoPage.obter_CorProduto());
		System.out.println(carrinhoPage.obter_QuantidadeProduto());
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_SubtotalProduto()));
		
		System.out.println("ITENS DE TOTAIS");
		
		System.out.println(Funcoes.removeTextoItemsDevolveInt(carrinhoPage.obter_NumeroItensTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_SubtotalTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_ShippingTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_TotalTaxExclTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_TotalTaxIncTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_TaxesTotal()));
		
		
		
		//Asser��es Hamcrest
		
		assertThat(carrinhoPage.obter_NomeProduto(),is(esperado_NomeProduto));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_PrecoProduto()),is(esperado_PrecoProduto));
		assertThat(carrinhoPage.obter_TamanhoProduto(),is(esperado_TamanhoProduto));
		assertThat(carrinhoPage.obter_CorProduto(),is(esperado_CorProduto));
		assertThat(Integer.parseInt(carrinhoPage.obter_QuantidadeProduto()),is(esperado_QuantidadeProduto));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_SubtotalProduto()),is(esperado_SubtotalProduto));
		
		assertThat(Funcoes.removeTextoItemsDevolveInt(carrinhoPage.obter_NumeroItensTotal()),is(esperado_NumeroItensTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_SubtotalTotal()),is(esperado_SubtotalTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_ShippingTotal()),is(esperado_ShippingTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_TotalTaxExclTotal()),is(esperado_TotalTaxExclTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_TotalTaxIncTotal()),is(esperado_TotalTaxIncTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_TaxesTotal()),is(esperado_TaxesTotal));
		
		//Asser��es JUnit
		/*
		assertEquals(esperado_NomeProduto, carrinhoPage.obter_NomeProduto());
		assertEquals(esperado_PrecoProduto, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_PrecoProduto()));
		assertEquals(esperado_TamanhoProduto, carrinhoPage.obter_TamanhoProduto());
		assertEquals(esperado_CorProduto, carrinhoPage.obter_CorProduto());
		assertEquals(esperado_QuantidadeProduto, Integer.parseInt(carrinhoPage.obter_QuantidadeProduto());
		assertEquals(esperado_SubtotalProduto, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_SubtotalProduto()));
		
		assertEquals(esperado_NumeroItensTotal, Funcoes.removeTextoItemsDevolveInt(carrinhoPage.obter_NumeroItensTotal()));
		assertEquals(esperado_SubtotalTotal, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_SubtotalTotal()));
		assertEquals(esperado_ShippingTotal, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_ShippingTotal()));
		assertEquals(esperado_TotalTaxExclTotal, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_TotalTaxExclTotal()));
		assertEquals(esperado_TotalTaxIncTotal, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_TotalTaxIncTotal()));
		assertEquals(esperado_TaxesTotal, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_TaxesTotal()));
		*/		
	
	}
	
	CheckoutPage checkoutPage;
	
	@Test
	public void testIrParaCheckout_FreteMeioPagamentoEnderecoListadosOk() {
		//Pr�-condi��es
		
		//Produto dispon�vel no carrinho de compras
		testIrParaCarrinho_InformacoesPersistidas();
		
		//Teste
		
		//Clicar no bot�o
		checkoutPage = carrinhoPage.clicarBotaoProceedToCheckout();
		
		//Preencher Informa��es
		
		//Validar Informa��oes na tela
		assertThat(Funcoes.removeCifraoDevolveDouble(checkoutPage.obter_totalTaxIncTotal()), is(esperado_TotalTaxIncTotal));
		//assertThat(checkoutPage.obter_nomeCliente(), is(esperado_nomeCliente));
		assertTrue(checkoutPage.obter_nomeCliente().startsWith(esperado_nomeCliente));
		
		checkoutPage.clicarBotaoContinueAddress();
		
		String encontrado_shippingValor = checkoutPage.obter_shippingValor();
		encontrado_shippingValor = Funcoes.removeTexto(encontrado_shippingValor, " tax excl.");
		Double encontrado_shippingValor_Double = Funcoes.removeCifraoDevolveDouble(encontrado_shippingValor);
		
		assertThat(encontrado_shippingValor_Double,is(esperado_ShippingTotal));
		
		checkoutPage.clicarbotaoContinueShipping();
		
		//Selecionar op��o "Pay by Check"
		checkoutPage.selecionarRadioPayByCheck();
		
		//Validar valor do cheque (amount)
		String encontrado_amountPayByCheck = checkoutPage.obter_amountPayByCheck();
		encontrado_amountPayByCheck = Funcoes.removeTexto(encontrado_amountPayByCheck, " (tax incl.)");
		Double encontrado_amountPayByCheck_Double = Funcoes.removeCifraoDevolveDouble(encontrado_amountPayByCheck);
		
		assertThat(encontrado_amountPayByCheck_Double,is(esperado_TotalTaxIncTotal));
		
		//Clicar na op��o "I agree"
		checkoutPage.selecionarCheckboxIAgree();
		
		assertTrue(checkoutPage.estaSelecionadoCheckboxIAgree());
		
	}
	
	PedidoPage pedidoPage;
	
	@Test
	public void testFinalizarPedido_PedidoFinalizadoComSucesso() {
		//Pr�-condi��es
		//Checkout completamente conclu�do
		testIrParaCheckout_FreteMeioPagamentoEnderecoListadosOk();
		
		//Teste
		
		//Clicar no bot�o confirmar o pedido
		pedidoPage = checkoutPage.clicarBotaoConfirmaPedido();		
		
		//Validar valores da tela
		
		//Caso n�o utiliza-se a fun��o criada para tirar o caracter especial, poderia ser validado da forma apresentada abaixo
		//assertTrue(pedidoPage.obter_textoPedidoConfirmado().endsWith("YOUR ORDER IS CONFIRMED"));
		
		assertThat(Funcoes.removeCaracterEspecial(pedidoPage.obter_textoPedidoConfirmado()).toUpperCase(), is("YOUR ORDER IS CONFIRMED"));
		assertThat(pedidoPage.obter_email(),is("testeWell@teste.com"));
		assertThat(pedidoPage.obter_totalProdutos(), is(esperado_SubtotalProduto));
		assertThat(pedidoPage.obter_totalTaxIncl(),is(esperado_TotalTaxIncTotal));
		assertThat(pedidoPage.obter_metodoPagamento(), is("check"));
		
	}


}
