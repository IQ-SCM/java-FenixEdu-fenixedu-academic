<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 <xsl:output method="xml" indent="yes"/>
 <xsl:template match="/"> 
	<xsl:apply-templates select="list"/> 
</xsl:template> 

<xsl:template match="list">
	<info-aluno>
	<xsl:apply-templates select="net.sourceforge.fenixedu.dataTransferObject.externalServices.InfoStudentExternalInformation"/>
	</info-aluno>
</xsl:template>

<xsl:template match="net.sourceforge.fenixedu.dataTransferObject.externalServices.InfoStudentExternalInformation">
		<xsl:apply-templates select="degree"/>
		<xsl:apply-templates select="person"/>
		<xsl:apply-templates select="courses"/>
						
</xsl:template> 

<xsl:template match="courses">
		<disciplinas>
			<xsl:apply-templates select="net.sourceforge.fenixedu.dataTransferObject.externalServices.InfoExternalEnrollmentInfo"/>	
		</disciplinas>
</xsl:template> 

<xsl:template match="net.sourceforge.fenixedu.dataTransferObject.externalServices.InfoExternalEnrollmentInfo">
		<codigo>
			<xsl:value-of select="./course/code"/>
		</codigo>
		<nome>
			<xsl:value-of select="./course/name"/>
		</nome>
		<nota>
			<xsl:value-of select="./finalGrade"/>
		</nota>		
</xsl:template> 

<xsl:template match="degree">
				<curso>
					<codigo>
						<xsl:value-of select="./code"/>
					</codigo>
					<nome>
						<xsl:value-of select="./name"/>
					</nome>
					<ramo>
						<xsl:apply-templates select="branch"/>
					</ramo>
				</curso>
</xsl:template> 

<xsl:template match="branch">
					<codigo>
						<xsl:value-of select="./code"/>
					</codigo>
					<nome>
						<xsl:value-of select="./name"/>
					</nome>
</xsl:template> 

<xsl:template match="person">
				<dados-pessoais>
					<nome>
						<xsl:value-of select="./name"/>
					</nome>
					<sexo>
						<xsl:value-of select="./sex"/>
					</sexo>
					<data-nascimento>
						<xsl:value-of select="./birthday"/>
					</data-nascimento>					
					<nacionalidade>
						<xsl:value-of select="./nationality"/>
					</nacionalidade>		
					<naturalidade>								
						<xsl:apply-templates select="citizenship"/>
					</naturalidade>
					<morada>
						<xsl:apply-templates select="address"/>
					</morada>
					<telefone>
						<xsl:value-of select="./phone"/>
					</telefone>
					<telemovel>
						<xsl:value-of select="./celularPhone"/>
					</telemovel>
					<e-mail>
						<xsl:value-of select="./email"/>
					</e-mail>
					<identificacao>
						<xsl:apply-templates select="identification"/>
					</identificacao>
					<nif>
						<xsl:value-of select="./fiscalNumber"/>
					</nif>
				</dados-pessoais>
</xsl:template>


<xsl:template match="address">
				<rua>
					<xsl:value-of select="./street"/>
				</rua>
				<codigo-postal>
					<xsl:value-of select="./postalCode"/>
				</codigo-postal>
				<localidade>
					<xsl:value-of select="./town"/>
				</localidade>				
</xsl:template> 

<xsl:template match="citizenship">
				<freguesia>
					<xsl:value-of select="./area"/>
				</freguesia>
				<concelho>
					<xsl:value-of select="./county"/>
				</concelho>
</xsl:template> 

<xsl:template match="identification">
				<tipo-documento>
					<xsl:value-of select="./documentType"/>
				</tipo-documento>
				<numero>
					<xsl:value-of select="./number"/>
				</numero>
</xsl:template> 


</xsl:stylesheet>
