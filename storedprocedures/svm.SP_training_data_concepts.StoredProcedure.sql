USE [PSCI_CCTS]
GO
/****** Object:  StoredProcedure [svm].[SP_training_data_concepts]    Script Date: 9/24/2018 9:24:26 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		George Michel
-- Create date: 
-- Description:	get runtype,doc,token
-- =============================================
CREATE PROCEDURE [svm].[SP_training_data_concepts] 
	@station  varchar(256),
	@organ varchar(256)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	select t1.document_id, t1.concept, t2.runtype from
(
SELECT ab.document_id ,ab.anno_base_id , annoT.canonicalForm concept 
  FROM etex.anno_token annoT, etex.anno_base ab
  where ab.anno_base_id = annoT.anno_base_id
  and annoT.canonicalForm is not null
 union
 SELECT ab.document_id ,ab.anno_base_id ,  aoc.code 
  FROM etex.anno_ontology_concept aoc, etex.anno_base ab
  where ab.anno_base_id = aoc.anno_base_id
 ) t1 
 , (select  ed.runtype ,ed.document_id,ed.instance_key,ed.instance_id RowID,ed.organ 
 from etex.document ed) t2
  where t2.document_id = t1.document_id
  and
  t2.instance_key=@station
  and
  t2.organ=@organ
END
GO
