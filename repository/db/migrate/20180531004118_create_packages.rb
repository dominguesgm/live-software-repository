class CreatePackages < ActiveRecord::Migration[5.1]
  def change
    create_table :packages do |t|
      t.string :package_name
      t.integer :class_count
      t.boolean :has_subpackages

      t.timestamps
    end
  end
end
