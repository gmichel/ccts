USE [PSCI_CCTS]
GO
/****** Object:  StoredProcedure [svm].[SP_lung_concept_doc]    Script Date: 9/24/2018 9:24:26 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		George Michel
-- Create date: 
-- Description:	get base,doc,token
-- =============================================
CREATE PROCEDURE [svm].[SP_lung_concept_doc] 
	
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	  select t1.document_id,t1.anno_base_id, t1.concept, t2.gold_std from
(
SELECT ab.document_id ,ab.anno_base_id , annoT.canonicalForm concept 
  FROM etex.anno_token annoT, etex.anno_base ab
  where ab.anno_base_id = annoT.anno_base_id
  and annoT.canonicalForm is not null
 union
 SELECT ab.document_id ,ab.anno_base_id ,  aoc.code 
  FROM etex.anno_ontology_concept aoc, etex.anno_base ab
  where ab.anno_base_id = aoc.anno_base_id
 ) t1 , (select eg.gold_std,ed.document_id,eg.RowID from svm.training_lung eg, etex.document ed
  where eg.RowID = ed.instance_id) t2
  where t2.document_id = t1.document_id
  -- order by t2.document_id desc
  
END
GO
